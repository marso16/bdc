package com.capitalbanking.stage.helper;

import com.capitalbanking.stage.shared.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class BccSwitchCallHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(BccSwitchCallHelper.class);

    private static final long EXPIRY_SAFETY_MARGIN_MS = 30_000L;

    private final BccParametrage bccParametrage;
    private final ObjectMapper objectMapper;

    private volatile String cachedAccessToken;
    private volatile long cachedAccessTokenExpiryMillis;

    public synchronized String getAccessToken() throws Exception {
        if (cachedAccessToken != null && System.currentTimeMillis() < cachedAccessTokenExpiryMillis) {
            LOGGER.info("getAccessToken -> reusing cached token (valid for {} more ms)",
                    cachedAccessTokenExpiryMillis - System.currentTimeMillis());
            return cachedAccessToken;
        }
        return requestNewAccessToken();
    }

    private String requestNewAccessToken() throws Exception {
        URL url = new URL(bccParametrage.getBaseUrl() + bccParametrage.getTokenUrl());

        StringBuilder sb = new StringBuilder();
        appendParam(sb, Constants.GRANT_TYPE, bccParametrage.getGrantType(), false);
        appendParam(sb, Constants.CLIENT_ID, bccParametrage.getClientId(), true);
        appendParam(sb, Constants.CLIENT_SECRET, bccParametrage.getClientSecret(), true);
        appendParam(sb, Constants.USERNAME, bccParametrage.getUsername(), true);
        appendParam(sb, Constants.PASSWORD, bccParametrage.getPassword(), true);
        appendParam(sb, Constants.SCOPE, bccParametrage.getScope(), true);
        byte[] body = sb.toString().getBytes(StandardCharsets.UTF_8);

        HttpURLConnection con = openConnection(url);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("Accept", "application/json");
        con.setConnectTimeout(Constants.CONNECT_TIMEOUT_MS);
        con.setReadTimeout(Constants.READ_TIMEOUT_MS);
        con.setDoOutput(true);

        LOGGER.info("getAccessToken -> POST {}", url);

        try (OutputStream os = con.getOutputStream()) {
            os.write(body);
        }

        int status = con.getResponseCode();
        String responseBody = readBody(status >= 400 ? con.getErrorStream() : con.getInputStream());
        logResponseHeaders(con);
        con.disconnect();

        LOGGER.info("getAccessToken -> HTTP {}", status);
        LOGGER.info("getAccessToken -> Response: {}", responseBody);

        if (status >= 400) {
            throw new BccHttpException(status, "Authentication failed: " + responseBody);
        }

        JSONObject json = new JSONObject(responseBody);
        if (json.has("error") && !json.getString("error").isEmpty()) {
            throw new BccHttpException(status,
                    "Authentication error: " + json.optString("error")
                            + " – " + json.optString("error_description"));
        }

        String accessToken = json.getString("access_token");

        long expiresInSeconds;
        try {
            expiresInSeconds = Long.parseLong(json.optString("expires_in", "0"));
        } catch (NumberFormatException e) {
            expiresInSeconds = 0;
        }

        cachedAccessToken = accessToken;
        cachedAccessTokenExpiryMillis = System.currentTimeMillis()
                + Math.max(0, expiresInSeconds * 1000 - EXPIRY_SAFETY_MARGIN_MS);
        LOGGER.info("getAccessToken -> cached new token, expires_in={}s", expiresInSeconds);

        return accessToken;
    }

    public void updatePaymentStatus(String token, Object requestBody) throws Exception {
        callBcc("UPDATE PAYMENT STATUS",
                bccParametrage.getBaseUrl() + bccParametrage.getPaymentUpdateUrl(),
                token, requestBody);
    }

    public void updateEnrollmentStatus(String token, Object requestBody) throws Exception {
        callBcc("UPDATE ENROLLMENT STATUS",
                bccParametrage.getBaseUrl() + bccParametrage.getEnrollmentUpdateUrl(),
                token, requestBody);
    }

    public JSONObject getEnrollmentFiles(String token, Object requestBody) throws Exception {
        return callBcc(
                "GET ENROLLMENT FILES",
                bccParametrage.getBaseUrl() + bccParametrage.getEnrollmentFileUrl(),
                token, requestBody);
    }

    private JSONObject callBcc(String operationLabel, String urlStr,
                               String token, Object requestBody) throws Exception {
        LOGGER.info("================================================================");
        LOGGER.info("BCC {} START", operationLabel);
        LOGGER.info("================================================================");

        String jsonBody = toJson(requestBody);
        LOGGER.info("REQUEST BODY: {}", jsonBody);

        URL url = new URL(urlStr);
        LOGGER.info("callBcc -> POST {}", urlStr);

        HttpURLConnection con = openConnection(url);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Authorization", Constants.BEARER + " " + token);
        con.setConnectTimeout(Constants.CONNECT_TIMEOUT_MS);
        con.setReadTimeout(Constants.READ_TIMEOUT_MS);
        con.setDoOutput(true);

        LOGGER.info("REQUEST HEADERS ================================================");
        LOGGER.info("Authorization : {} {}", Constants.BEARER, token);
        LOGGER.info("Content-Type  : application/json");
        LOGGER.info("Accept        : application/json");
        LOGGER.info("================================================================");

        if (jsonBody != null && !jsonBody.isEmpty()) {
            LOGGER.info("BODY SIZE : {} bytes", jsonBody.getBytes(StandardCharsets.UTF_8).length);
            LOGGER.info("Body: {}", jsonBody);
            try (OutputStream os = con.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }
        }

        int status = con.getResponseCode();

        LOGGER.info("================================================================");
        LOGGER.info("RESPONSE STATUS  : {}", status);
        LOGGER.info("RESPONSE MESSAGE : {}", con.getResponseMessage());
        LOGGER.info("CONTENT TYPE     : {}", con.getContentType());

        logResponseHeaders(con);

        String locationHeader = con.getHeaderField("Location");
        if (locationHeader != null) {
            LOGGER.warn("REDIRECT LOCATION DETECTED : {}", locationHeader);
        }

        String responseBody = readBody(status >= 400 ? con.getErrorStream() : con.getInputStream());
        con.disconnect();

        LOGGER.info("RESPONSE BODY ================================================");
        LOGGER.info("{}", responseBody);
        LOGGER.info("================================================================");
        LOGGER.info("BCC {} END", operationLabel);
        LOGGER.info("================================================================");

        if (status >= 400) {
            throw new BccHttpException(status, responseBody);
        }

        if (responseBody.trim().startsWith("<")) {
            throw new BccHttpException(0, "Unexpected HTML response: " + responseBody);
        }

        return new JSONObject(responseBody);
    }

    private HttpURLConnection openConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }

    private void logResponseHeaders(HttpURLConnection con) {
        LOGGER.info("RESPONSE HEADERS ===============================================");
        for (Map.Entry<String, List<String>> h : con.getHeaderFields().entrySet()) {
            LOGGER.info("{} : {}", h.getKey(), h.getValue());
        }
        LOGGER.info("================================================================");
    }

    private String readBody(InputStream stream) throws IOException {
        if (stream == null) return "";
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) result.append(line);
            return result.toString();
        }
    }

    private static void appendParam(StringBuilder sb, String key, String value,
                                    boolean ampersandFirst) {
        if (value == null || value.trim().isEmpty()) return;
        try {
            if (ampersandFirst && sb.length() > 0) sb.append('&');
            sb.append(URLEncoder.encode(key, Constants.UTF_8_ENCODING))
                    .append('=')
                    .append(URLEncoder.encode(value, Constants.UTF_8_ENCODING));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported", e);
        }
    }

    private String toJson(Object obj) throws Exception {
        if (obj instanceof String) return (String) obj;
        if (obj instanceof JSONObject) return obj.toString();
        return objectMapper.writeValueAsString(obj);
    }

    @Getter
    public static class BccHttpException extends RuntimeException {
        private final int httpStatus;

        public BccHttpException(int httpStatus, String message) {
            super(message);
            this.httpStatus = httpStatus;
        }
    }
}