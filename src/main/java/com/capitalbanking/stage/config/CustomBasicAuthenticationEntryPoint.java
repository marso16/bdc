
package com.capitalbanking.stage.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomBasicAuthenticationEntryPoint.class);

    private final ObjectMapper objectMapper;

    public CustomBasicAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
            throws IOException {
        response.addHeader("WWW-Authenticate", "Basic realm=\"" + getRealmName() + "\"");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.println(generateRestErrorContent("401", authEx.getMessage()));
    }

    public String generateRestErrorContent(String errorCode, String errorMessage) {
        String responseBody = "";
        try {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", errorCode);
            errorResponse.put("error_description", errorMessage);
            responseBody = objectMapper.writeValueAsString(errorResponse);
        } catch (Exception e) {
            LOGGER.error("{}", String.valueOf(e));
        }
        return responseBody;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setRealmName("CBS_WEBSERVER");
        super.afterPropertiesSet();
    }
}