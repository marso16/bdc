package com.capitalbanking.stage.config;

import com.capitalbanking.stage.repository.ri_commons.RiCommonsApiCallsDao;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;

@Component
@RequiredArgsConstructor
public class RiCommonsApiCallsInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RiCommonsApiCallsInterceptor.class);

    private static final String ATTR_START_NS = "traceStartNs";
    private static final String ATTR_TRANS_ID = "traceTransId";
    private static final String ATTR_REQ_BODY = "cachedRequestBody";

    private final RiCommonsApiCallsDao riCommonsApiCallsDao;
    private final ObjectMapper mapper;
    private String serverName;

    @PostConstruct
    void init() {
        try {
            this.serverName = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            this.serverName = "unknown";
            LOGGER.warn("Could not resolve hostname", e);
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        LOGGER.info("preHandle instance={} uri={}", System.identityHashCode(this), req.getRequestURI());

        String uri = req.getRequestURI();

        if ((uri != null && uri.endsWith("/oauth/token"))
                || (uri != null && uri.endsWith("/error"))
                || (uri != null && uri.endsWith("/annulation"))) {
            return true;
        }

        req.setAttribute(ATTR_START_NS, System.nanoTime());

        String typeOperation;
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            typeOperation = hm.getBeanType().getSimpleName() + "#" + hm.getMethod().getName();
        } else {
            typeOperation = (uri != null ? uri : "UNKNOWN");
        }

        String requestBody = (String) req.getAttribute(ATTR_REQ_BODY);

        String transId = (String) req.getAttribute("transId");
        req.setAttribute(ATTR_TRANS_ID, transId);

        if (transId == null || transId.trim().isEmpty()) {
            return true;
        }

        String clientName = (String) req.getAttribute("clientUserPrincipal");
        String clientIp = getClientIp(req);


        boolean created;
        try {
            created = riCommonsApiCallsDao.reserveTraceIfAbsent(
                    transId, typeOperation, requestBody, clientName, clientIp, serverName);
        } catch (Exception e) {
            LOGGER.warn("reserveTraceIfAbsent failed: {}", e.getMessage());
            return true;
        }

        if (!created) {
            sendConflict(res, transId);
            return false;
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex) {
        String responseBody = safeReadResponseBody(req);

        String transId = (String) req.getAttribute(ATTR_TRANS_ID);
        if (transId == null || transId.trim().isEmpty()) {
            return;
        }

        Long startNs = (Long) req.getAttribute(ATTR_START_NS);
        long durationMs = (startNs == null) ? 0L : (System.nanoTime() - startNs) / 1_000_000L;


        try {
            riCommonsApiCallsDao.updateAfterCompletion(transId, responseBody, durationMs, res.getStatus());
        } catch (Exception e) {
            LOGGER.warn("trace updateAfterCompletion failed: {}", e.getMessage());
        }
    }

    private void sendConflict(HttpServletResponse res, String transId) throws IOException {
        res.setStatus(HttpServletResponse.SC_CONFLICT); // 409
        res.setContentType("application/json");
        JsonNode payload = mapper.createObjectNode()
                .put("error", "00409")
                .put("error_description", "transId already exists")
                .put("transId", transId);
        res.getWriter().write(mapper.writeValueAsString(payload));
        res.getWriter().flush();
    }

    private String safeReadResponseBody(HttpServletRequest req) {
        try {
            Object attr = req.getAttribute("ccr");
            if (attr instanceof ContentCachingResponseWrapper) {
                ContentCachingResponseWrapper ccr = (ContentCachingResponseWrapper) attr;
                byte[] bytes = ccr.getContentAsByteArray();
                if (bytes.length == 0) {
                    return "";
                }
                String enc = (ccr.getCharacterEncoding() != null) ? ccr.getCharacterEncoding() : "UTF-8";
                return new String(bytes, java.nio.charset.Charset.forName(enc));
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        LOGGER.debug("clientIp={}", ip);
        return ip;
    }

}
