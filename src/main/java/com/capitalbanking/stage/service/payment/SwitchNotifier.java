package com.capitalbanking.stage.service.payment;

import com.capitalbanking.stage.helper.BccSwitchCallHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SwitchNotifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwitchNotifier.class);

    private final BccSwitchCallHelper bccSwitchCallHelper;

    @Async
    public void notifyPayment(String issuertrxref, String vouchercode,
                              String intent, String state) {
        try {
            String token = bccSwitchCallHelper.getAccessToken();
            Map<String, String> body = new LinkedHashMap<>();
            body.put("vouchercode", vouchercode);
            body.put("issuertrxref", issuertrxref);
            body.put("intent", intent);
            body.put("state", state);
            body.put("updatetime", String.valueOf(System.currentTimeMillis()));
            bccSwitchCallHelper.updatePaymentStatus(token, body);
            LOGGER.info("notifyPayment OK: issuertrxref={} intent={} state={}", issuertrxref, intent, state);
        } catch (Exception e) {
            LOGGER.error("notifyPayment FAILED: issuertrxref={} intent={} state={}", issuertrxref, intent, state, e);
        }
    }

    @Async
    public void notifyEnrollment(String requestId, String status, String reason) {
        try {
            String token = bccSwitchCallHelper.getAccessToken();
            Map<String, String> body = new LinkedHashMap<>();
            body.put("requestID", requestId);
            body.put("status", status);
            body.put("reason", reason);
            bccSwitchCallHelper.updateEnrollmentStatus(token, body);
            LOGGER.info("notifyEnrollment OK: requestId={} status={}", requestId, status);
        } catch (Exception e) {
            LOGGER.error("notifyEnrollment FAILED: requestId={} status={}", requestId, status, e);
        }
    }
}