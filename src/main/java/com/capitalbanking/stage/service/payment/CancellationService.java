package com.capitalbanking.stage.service.payment;

import com.capitalbanking.stage.config.ApiResult;
import com.capitalbanking.stage.model.bdc.DebitAnnulationRequest;
import com.capitalbanking.stage.model.bdc.DebitAnnulationResponse;
import com.capitalbanking.stage.model.ri_commons.CancelTransRequest;
import com.capitalbanking.stage.model.ri_commons.CancelTransResult;
import com.capitalbanking.stage.service.ri_commons.RiTransferService;
import com.capitalbanking.stage.shared.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CancellationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CancellationService.class);

    private final RiTransferService riTransferService;

    public CancellationService(RiTransferService riTransferService) {
        this.riTransferService = riTransferService;
    }

    public ApiResult<DebitAnnulationResponse> cancel(DebitAnnulationRequest request) {
        DebitAnnulationResponse response = new DebitAnnulationResponse();

        try {
            if (request.getIssuertrxref() == null || request.getIssuertrxref().trim().isEmpty()) {
                return error(response, Constants.ERR_CODE_1000, Constants.ERR_MSG_1000);
            }

            if (request.getVouchercode() == null || request.getVouchercode().trim().isEmpty()) {
                return error(response, Constants.ERR_CODE_399, Constants.ERR_MSG_399);
            }

            CancelTransRequest cancelReq = CancelTransRequest.builder()
                    .transId(request.getIssuertrxref())
                    .annulTransId(request.getIssuertrxref())
                    .date(LocalDate.now())
                    .build();

            LOGGER.info("debitAnnulation -> cancelTrans: transId={}, annulTransId={}, intent={}",
                    cancelReq.getTransId(), cancelReq.getAnnulTransId(), request.getIntent());

            CancelTransResult result = riTransferService.cancelTrans(cancelReq);

            if (result == null) {
                LOGGER.error("cancelTrans returned null for transId={}", request.getIssuertrxref());
                return error(response, Constants.ERR_CODE_399, Constants.ERR_MSG_399);
            }

            if (Constants.STATUS_OK.equalsIgnoreCase(result.getStatus())) {
                response.setError("");
                response.setError_description("");
                return ApiResult.ok(response);
            } else {
                response.setError(result.getErrorCode());
                response.setError_description(result.getErrorMsg());
                return ApiResult.badRequest(response);
            }

        } catch (Exception e) {
            LOGGER.error("==debitAnnulation Error===", e);
            return error(response, Constants.ERR_CODE_399, Constants.ERR_MSG_399);
        }
    }

    private ApiResult<DebitAnnulationResponse> error(DebitAnnulationResponse response,
                                                     String code, String description) {
        response.setError(code);
        response.setError_description(description);
        return ApiResult.badRequest(response);
    }
}