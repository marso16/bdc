package com.capitalbanking.stage.service.payment;

import com.capitalbanking.stage.config.ApiResult;
import com.capitalbanking.stage.model.bdc.PingCGBRequest;
import com.capitalbanking.stage.model.bdc.PingCGBResponse;
import com.capitalbanking.stage.service.core.GlobalCoreService;
import com.capitalbanking.stage.shared.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PingService.class);

    private final GlobalCoreService globalCoreService;

    public PingService(GlobalCoreService globalCoreService) {
        this.globalCoreService = globalCoreService;
    }

    // ----------------------------------------------------- ping CGB
    public ApiResult<PingCGBResponse> pingCGB(PingCGBRequest request) {
        PingCGBResponse response = new PingCGBResponse();
        response.setIssuertrxref(request.getIssuertrxref());
        try {
            boolean cutOff = globalCoreService.isCutOffTimeByFlag(Constants.FLAG_DISPONIBLE);
            response.setAvailable(cutOff ? Constants.FLAG_FALSE : Constants.FLAG_TRUE);
            return ApiResult.ok(response);
        } catch (Exception e) {
            LOGGER.error("==Ping Error===", e);
            response.setAvailable(Constants.FLAG_FALSE);
            response.setError(Constants.ERR_CODE_500);
            response.setError_description(Constants.ERR_MSG_500);
            return ApiResult.badRequest(response);
        }
    }
}