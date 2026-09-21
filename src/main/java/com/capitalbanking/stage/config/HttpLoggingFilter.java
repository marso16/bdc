package com.capitalbanking.stage.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class HttpLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger("http.trace");
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final int maxBodyChars;
    private final Set<String> skipPaths;
    private final Set<String> sensitiveKeys;
    private final Pattern jsonMediaType = Pattern.compile("(?i)application/(json|.*\\+json)");

    public HttpLoggingFilter(int maxBodyChars, Set<String> skipPaths, Set<String> sensitiveKeys) {
        this.maxBodyChars = maxBodyChars;
        this.skipPaths = skipPaths;
        this.sensitiveKeys = new HashSet<>();
        for (String k : sensitiveKeys) {
            this.sensitiveKeys.add(k.toLowerCase(Locale.ROOT));
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        for (String skip : skipPaths) {
            if (path.startsWith(skip)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String correlationId = request.getHeader("X-Correlation-Id");
        if (!StringUtils.hasText(correlationId)) {
            correlationId = UUID.randomUUID().toString();
        }
        MDC.put("corr", correlationId);

        Instant start = Instant.now();

        CachedBodyHttpServletRequest req = new CachedBodyHttpServletRequest(request);
        ContentCachingResponseWrapper res = new ContentCachingResponseWrapper(response);

        String rawReqBody = new String(req.getCachedBody(), Optional.ofNullable(req.getCharacterEncoding()).orElse("UTF-8"));
        request.setAttribute("cachedRequestBody", rawReqBody);

        request.setAttribute("ccr", res);

        String transId = tryExtractTransIdFromJson(rawReqBody);
        if (transId == null) {
            transId = request.getHeader("X-Trans-Id");
        }
        if (transId == null) {
            transId = request.getParameter("transId");
        }
        if (StringUtils.hasText(transId)) {
            request.setAttribute("transId", transId);
        }

        try {
            chain.doFilter(req, res);
        } finally {
            long durationMs = java.time.Duration.between(start, Instant.now()).toMillis();

            String method = request.getMethod();
            String uri = request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");

            String reqBody = normalizeBody(rawReqBody, request.getContentType());
            String resBody = extractResponseBody(res);

            request.setAttribute("capturedResponseBody", resBody);

            String message =
                    "\n==================== HTTP ====================\n" +
                            "Service : " + method + " " + uri + " (" + durationMs + "ms)\n" +
                            "---- Request Body ----\n" + reqBody +
                            "\n---- Response Body ----\n" + resBody +
                            "\n==============================================";

            log.info(message);

            res.copyBodyToResponse();
            MDC.remove("corr");
        }
    }

    private String extractResponseBody(ContentCachingResponseWrapper res) throws IOException {
        String body = bytesToString(res.getContentAsByteArray(), res.getCharacterEncoding());
        return normalizeBody(body, res.getContentType());
    }

    private String bytesToString(byte[] bytes, String encoding) {
        if (bytes == null || bytes.length == 0) {
            return "(empty)";
        }
        Charset cs = Charset.forName(encoding != null ? encoding : "UTF-8");
        String s = new String(bytes, cs);
        if (s.length() > maxBodyChars) {
            return s.substring(0, maxBodyChars) + " ...[truncated]";
        }
        return s;
    }

    private String normalizeBody(String raw, String contentType) {
        if (raw == null || "(empty)".equals(raw)) {
            return "(empty)";
        }
        if (contentType != null && jsonMediaType.matcher(contentType).find()) {
            try {
                JsonNode n = MAPPER.readTree(raw);
                JsonNode masked = maskJson(n);
                return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(masked);
            } catch (Exception ignore) {

            }
        }
        return raw;
    }

    private JsonNode maskJson(JsonNode node) {
        if (node == null) {
            return null;
        }
        if (node.isObject()) {
            Iterator<String> names = node.fieldNames();
            List<String> keys = new ArrayList<>();
            while (names.hasNext()) {
                keys.add(names.next());
            }
            for (String name : keys) {
                JsonNode child = node.get(name);
                if (sensitiveKeys.contains(name.toLowerCase(Locale.ROOT))) {
                    ((com.fasterxml.jackson.databind.node.ObjectNode) node).put(name, "[REDACTED]");
                } else {
                    ((com.fasterxml.jackson.databind.node.ObjectNode) node).set(name, maskJson(child));
                }
            }
        } else if (node.isArray()) {
            for (int i = 0; i < node.size(); i++) {
                ((com.fasterxml.jackson.databind.node.ArrayNode) node).set(i, maskJson(node.get(i)));
            }
        }
        return node;
    }

    private String tryExtractTransIdFromJson(String raw) {
        if (raw == null || raw.trim().isEmpty()) return null;
        try {
            JsonNode n = MAPPER.readTree(raw);
            for (String field : new String[]{"issuertrxref", "transId"}) {
                JsonNode tid = n.get(field);
                if (tid != null && !tid.isNull() && !tid.asText().isEmpty()) {
                    return tid.asText();
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
