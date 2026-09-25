package com.capitalbanking.stage.service.payment;

import com.capitalbanking.stage.shared.ApiResult;
import com.capitalbanking.stage.model.bdc.CompteCreationRequest;
import com.capitalbanking.stage.model.bdc.CompteCreationResponse;
import com.capitalbanking.stage.model.ri_commons.CreateAccountRequest;
import com.capitalbanking.stage.model.ri_commons.CreateAccountResponse;
import com.capitalbanking.stage.service.ri_commons.RiTransferService;
import com.capitalbanking.stage.shared.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AccountCreationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountCreationService.class);

    private final RiTransferService riTransferService;
    private final SwitchNotifier switchNotifier;

    public AccountCreationService(RiTransferService riTransferService,
                                  SwitchNotifier switchNotifier) {
        this.riTransferService = riTransferService;
        this.switchNotifier = switchNotifier;
    }

    public ApiResult<CompteCreationResponse> createAccount(CompteCreationRequest request) {
        CompteCreationResponse response = new CompteCreationResponse();

        try {
            CreateAccountRequest procReq = CreateAccountRequest.builder()
                    .transId(request.getRequestID())
                    .bank(request.getBank())
                    .firstName(request.getFirstName())
                    .middleName(request.getMiddleName())
                    .surname(request.getSurname())
                    .lastName(request.getLastName())
                    .gender(request.getGender())
                    .birthdate(request.getBirthdate())
                    .gsm(request.getGsm())
                    .email(request.getEmail())
                    .address(request.getAddress())
                    .nationality(request.getNationality())
                    .country(request.getCountry())
                    .region(request.getRegion())
                    .city(request.getCity())
                    .identityType(request.getIdentityType())
                    .identityValue(request.getIdentityValue())
                    .build();

            LOGGER.info("compteCreation -> createAccount: transId={}, firstName={}, lastName={}, identityType={}, identityValue={}",
                    procReq.getTransId(), procReq.getFirstName(), procReq.getLastName(),
                    procReq.getIdentityType(), procReq.getIdentityValue());

            CreateAccountResponse procResp = riTransferService.createAccount(procReq);

            if (procResp == null) {
                LOGGER.error("createAccount returned null for transId={}", request.getRequestID());
                return error(response);
            }

            response.setError(procResp.getErrorCode());
            response.setError_description(procResp.getErrorMsg());

            boolean ok = Constants.STATUS_OK.equalsIgnoreCase(procResp.getStatus());
            switchNotifier.notifyEnrollment(
                    request.getRequestID(),
                    ok ? Constants.STATE_ACCEPTED : Constants.STATE_REJECTED,
                    ok ? "Compte créé avec succès" : procResp.getErrorMsg());
            return ok ? ApiResult.ok(response) : ApiResult.badRequest(response);

        } catch (Exception e) {
            LOGGER.error("==compteCreation Error===", e);
            return error(response);
        }
    }

    private ApiResult<CompteCreationResponse> error(CompteCreationResponse response) {
        response.setError(Constants.ERR_CODE_399);
        response.setError_description(Constants.ERR_MSG_399);
        return ApiResult.badRequest(response);
    }
}