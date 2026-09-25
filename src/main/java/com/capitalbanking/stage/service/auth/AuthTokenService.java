package com.capitalbanking.stage.service.auth;

import com.capitalbanking.stage.shared.ApiResult;
import com.capitalbanking.stage.security.OAuthTokenResponse;
import com.capitalbanking.stage.security.TokenHelper;
import com.capitalbanking.stage.security.User;
import com.capitalbanking.stage.shared.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthTokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenService.class);

    private final AuthenticationManager authenticationManager;
    private final TokenHelper tokenHelper;

    @Value("${security.oauth.client-id}")
    private String expectedClientId;

    @Value("${security.oauth.client-secret}")
    private String expectedClientSecret;

    public AuthTokenService(AuthenticationManager authenticationManager, TokenHelper tokenHelper) {
        this.authenticationManager = authenticationManager;
        this.tokenHelper = tokenHelper;
    }

    public ApiResult<OAuthTokenResponse> issueToken(HttpServletRequest httpRequest) {
        OAuthTokenResponse response = new OAuthTokenResponse();

        try {
            Map<String, String> params = parseFormBody(httpRequest);

            String clientId = params.getOrDefault(Constants.CLIENT_ID, "").trim();
            String clientSecret = params.getOrDefault(Constants.CLIENT_SECRET, "").trim();
            String username = params.getOrDefault(Constants.USERNAME, "").trim();
            String password = params.getOrDefault(Constants.PASSWORD, "").trim();
            String scope = params.getOrDefault(Constants.SCOPE, Constants.SCOPE_READ).trim();

            if (!expectedClientId.equals(clientId) || !expectedClientSecret.equals(clientSecret)) {
                response.setError("invalid_client");
                response.setErrorDescription("Invalid client credentials");
                return ApiResult.status(HttpStatus.UNAUTHORIZED, response);
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = (User) authentication.getPrincipal();

            String accessToken = tokenHelper.generateToken(user.getUsername());
            String refreshToken = tokenHelper.generateToken(user.getUsername() + Constants._REFRESH);

            response.setAccessToken(accessToken);
            response.setTokenType(Constants.BEARER);
            response.setRefreshToken(refreshToken);
            response.setExpiresIn(tokenHelper.getExpiredIn());
            response.setScope(scope.isEmpty() ? Constants.SCOPE_READ : scope);

            return ApiResult.ok(response);

        } catch (AuthenticationException ex) {
            LOGGER.error("OAuth authentication failed: {}", ex.getMessage());
            response.setError("invalid_grant");
            response.setErrorDescription("Bad credentials");
            return ApiResult.status(HttpStatus.UNAUTHORIZED, response);
        } catch (Exception e) {
            LOGGER.error("OAuth token generation error", e);
            response.setError("server_error");
            response.setErrorDescription("Internal server error");
            return ApiResult.status(HttpStatus.INTERNAL_SERVER_ERROR, response);
        }
    }

    private Map<String, String> parseFormBody(HttpServletRequest httpRequest) throws Exception {
        String body = new String(
                StreamUtils.copyToByteArray(httpRequest.getInputStream()), StandardCharsets.UTF_8);

        Map<String, String> params = new HashMap<>();
        for (String pair : body.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                params.put(
                        URLDecoder.decode(kv[0], Constants.UTF_8_ENCODING),
                        URLDecoder.decode(kv[1], Constants.UTF_8_ENCODING));
            }
        }
        return params;
    }
}
