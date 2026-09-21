package com.capitalbanking.stage.helper;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class BccParametrage {

    @Value("${bcc.switch.base-url}")
    private String baseUrl;

    @Value("${bcc.switch.token-url}")
    private String tokenUrl;

    @Value("${bcc.switch.payment-update-url}")
    private String paymentUpdateUrl;

    @Value("${bcc.switch.enrollment-update-url}")
    private String enrollmentUpdateUrl;

    @Value("${bcc.switch.enrollment-file-url}")
    private String enrollmentFileUrl;

    @Value("${bcc.switch.client-id}")
    private String clientId;

    @Value("${bcc.switch.client-secret}")
    private String clientSecret;

    @Value("${bcc.switch.username}")
    private String username;

    @Value("${bcc.switch.password}")
    private String password;

    @Value("${bcc.switch.grant-type}")
    private String grantType;

    @Value("${bcc.switch.scope}")
    private String scope;
}