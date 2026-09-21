package com.capitalbanking.stage.service.payment;

import com.capitalbanking.stage.config.ApiResult;
import com.capitalbanking.stage.model.bdc.DepotArgentRequest;
import com.capitalbanking.stage.model.bdc.DepotArgentResponse;
import com.capitalbanking.stage.model.bdc.RetraitArgentRequest;
import com.capitalbanking.stage.model.bdc.RetraitArgentResponse;
import com.capitalbanking.stage.model.core.Compte;
import com.capitalbanking.stage.model.ri_commons.SaveInternalRequestRequest;
import com.capitalbanking.stage.model.ri_commons.SaveInternalRequestResponse;
import com.capitalbanking.stage.repository.core.CoreRepository;
import com.capitalbanking.stage.service.core.CompteService;
import com.capitalbanking.stage.service.ri_commons.RiTransferService;
import com.capitalbanking.stage.shared.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TransferService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferService.class);

    private final CompteService compteService;
    private final CoreRepository coreRepository;
    private final RiTransferService riTransferService;
    private final SwitchNotifier switchNotifier;

    public TransferService(CompteService compteService,
                           CoreRepository coreRepository,
                           RiTransferService riTransferService,
                           SwitchNotifier switchNotifier) {
        this.compteService = compteService;
        this.coreRepository = coreRepository;
        this.riTransferService = riTransferService;
        this.switchNotifier = switchNotifier;
    }

    // ---------------------------------------------------------------- depot
    public ApiResult<DepotArgentResponse> depot(DepotArgentRequest request) {
        DepotArgentResponse response = new DepotArgentResponse();
        response.setIssuertrxref(request.getIssuertrxref());

        try {
            String bankCodbnq = coreRepository.getBankCodbnq();

            String fromAccount = (request.getFromaccount() == null || request.getFromaccount().trim().isEmpty())
                    ? null : request.getFromaccount().trim();

            boolean isExternalMember = !bankCodbnq.equals(request.getFrommember());

            if (isExternalMember && fromAccount != null) {
                return depotError(response, Constants.ERR_CODE_399,
                        "fromaccount must be empty when frommember is an external participant");
            }

            if (fromAccount == null) {
                if (isExternalMember) {
                    fromAccount = coreRepository.getDepotPoolAccount();
                    LOGGER.info("depot: fromaccount is blank and frommember is external → using pool account: {}", fromAccount);
                    if (fromAccount == null) {
                        return depotError(response, Constants.ERR_CODE_399,
                                "Pool account not configured for DEPOT in fx5y8");
                    }
                } else {
                    return depotError(response, Constants.ERR_CODE_304,
                            "fromaccount is required when frommember is your own bank");
                }
            }

            if (fromAccount.equals(request.getAccountnumber() != null
                    ? request.getAccountnumber().trim() : "")) {
                return depotError(response, Constants.ERR_CODE_300, Constants.ERR_MSG_300);
            }

            Compte payerCompte = compteService.findCompteByCompte(fromAccount);
            if (payerCompte == null) {
                return depotError(response, Constants.ERR_CODE_301,
                        "Payer account not found: " + fromAccount);
            }

            Compte benefCompte = compteService.findCompteByCompte(request.getAccountnumber().trim());
            if (benefCompte == null) {
                return depotError(response, Constants.ERR_CODE_301,
                        "Beneficiary account not found: " + request.getAccountnumber());
            }

            String clientType = bankCodbnq.equals(request.getFrommember())
                    ? Constants.MODEV_X4_CLICLI
                    : Constants.MODEV_X4_CLINCLI;
            String modev = coreRepository.getModev(Constants.MODEV_X1_DEPOT, clientType);

            SaveInternalRequestRequest internalReq = SaveInternalRequestRequest.builder()
                    .transId(request.getIssuertrxref())
                    .refrel(request.getIssuertrxref())
                    .paymentDate(LocalDate.now())
                    .motif1(request.getDescription() != null
                            ? request.getDescription()
                            : request.getIssuertrxref())
                    .payerAccount(fromAccount)
                    .benefName(benefCompte.getClient().getClient())
                    .benefAccount(request.getAccountnumber())
                    .benefBicCode(payerCompte.getClient().getClient())
                    .amount(Double.parseDouble(request.getAmount()))
                    .benefCurrency(request.getCurrency())
                    .modev(modev)
                    .callSrc(Constants.CALL_SRC_VIRINT)
                    .build();

            LOGGER.info("depot -> saveInternalRequest: transId={}, payerAccount={}, benefAccount={}, amount={}, fromMember={}, " +
                            "toMember={}, clientType={}, modev={}",
                    internalReq.getTransId(), internalReq.getPayerAccount(), internalReq.getBenefAccount(),
                    internalReq.getAmount(), bankCodbnq, request.getTomember(), clientType, modev);

            SaveInternalRequestResponse internalResp = riTransferService.saveInternalRequest(internalReq);

            if (internalResp == null) {
                LOGGER.error("saveInternalRequest returned null for transId={}", request.getIssuertrxref());
                return depotError(response, Constants.ERR_CODE_399, Constants.ERR_MSG_399);
            }

            response.setError(internalResp.getErrorCode());
            response.setError_description(internalResp.getErrorMsg());
            response.setAcquirertrxref(internalResp.getBankReference());

            boolean ok = Constants.STATUS_OK.equalsIgnoreCase(internalResp.getStatus());
            switchNotifier.notifyPayment(request.getIssuertrxref(), request.getVouchercode(),
                    Constants.INTENT_DIRECT_CASH_IN, ok ? Constants.STATE_ACCEPTED : Constants.STATE_REJECTED);
            return ok ? ApiResult.ok(response) : ApiResult.badRequest(response);

        } catch (Exception e) {
            LOGGER.error("==depot Error===", e);
            return depotError(response, Constants.ERR_CODE_399, Constants.ERR_MSG_399);
        }
    }

    private ApiResult<DepotArgentResponse> depotError(DepotArgentResponse response,
                                                      String code, String description) {
        response.setError(code);
        response.setError_description(description);
        return ApiResult.badRequest(response);
    }

    // -------------------------------------------------------------- retrait
    public ApiResult<RetraitArgentResponse> retrait(RetraitArgentRequest request) {
        RetraitArgentResponse response = new RetraitArgentResponse();
        response.setIssuertrxref(request.getIssuertrxref());

        try {
            String bankCodbnq = coreRepository.getBankCodbnq();

            String accountNumber = (request.getAccountnumber() == null || request.getAccountnumber().trim().isEmpty())
                    ? null : request.getAccountnumber().trim();

            boolean isExternalTomember = !bankCodbnq.equals(request.getTomember());

            if (isExternalTomember && accountNumber != null) {
                return retraitError(response, Constants.ERR_CODE_399,
                        "accountnumber must be empty when tomember is an external participant");
            }

            if (accountNumber == null) {
                if (isExternalTomember) {
                    accountNumber = coreRepository.getRetraitPoolAccount();
                    LOGGER.info("retrait: accountnumber is blank and tomember is external → using pool account: {}", accountNumber);
                    if (accountNumber == null) {
                        return retraitError(response, Constants.ERR_CODE_399,
                                "Pool account not configured for RETRAIT in fx5y8");
                    }
                } else {
                    return retraitError(response, Constants.ERR_CODE_304,
                            "accountnumber is required when tomember is your own bank");
                }
            }

            String otp = extractOtp(request);
            if (otp == null || otp.trim().isEmpty()) {
                return retraitError(response, Constants.ERR_CODE_399,
                        "OTP is required in additionaldata");
            }

            if (request.getFromaccount().trim().equals(accountNumber)) {
                return retraitError(response, Constants.ERR_CODE_300, Constants.ERR_MSG_300);
            }

            Compte payerCompte = compteService.findCompteByCompte(request.getFromaccount().trim());
            if (payerCompte == null) {
                return retraitError(response, Constants.ERR_CODE_301,
                        "Customer account not found: " + request.getFromaccount());
            }

            Compte agentCompte = compteService.findCompteByCompte(accountNumber);
            if (agentCompte == null) {
                return retraitError(response, Constants.ERR_CODE_301,
                        "Agent account not found: " + accountNumber);
            }

            if (!bankCodbnq.equals(request.getFrommember())) {
                return retraitError(response, Constants.ERR_CODE_399,
                        "frommember '" + request.getFrommember() + "' is not valid");
            }

            String toMember = request.getTomember();

            String clientType = bankCodbnq.equals(request.getTomember())
                    ? Constants.MODEV_X4_CLICLI
                    : Constants.MODEV_X4_CLINCLI;
            String modev = coreRepository.getModev(Constants.MODEV_X1_RETRAIT, clientType);

            SaveInternalRequestRequest internalReq = SaveInternalRequestRequest.builder()
                    .transId(request.getIssuertrxref())
                    .refrel(request.getIssuertrxref())
                    .paymentDate(LocalDate.now())
                    .motif1(request.getDescription() != null
                            ? request.getDescription()
                            : request.getIssuertrxref())
                    .payerAccount(request.getFromaccount())
                    .benefName(toMember)
                    .benefAccount(accountNumber)
                    .benefBicCode(bankCodbnq)
                    .amount(Double.parseDouble(request.getAmount()))
                    .benefCurrency(request.getCurrency())
                    .modev(modev)
                    .callSrc(Constants.CALL_SRC_VIRINT)
                    .build();

            LOGGER.info("retrait -> saveInternalRequest: transId={}, payerAccount={}, benefAccount={}, amount={}, fromMember={}, " +
                            "toMember={}, clientType={}, modev={}",
                    internalReq.getTransId(), internalReq.getPayerAccount(), internalReq.getBenefAccount(),
                    internalReq.getAmount(), bankCodbnq, toMember, clientType, modev);

            SaveInternalRequestResponse internalResp = riTransferService.saveInternalRequest(internalReq);

            if (internalResp == null) {
                LOGGER.error("saveInternalRequest returned null for transId={}", request.getIssuertrxref());
                return retraitError(response, Constants.ERR_CODE_399, Constants.ERR_MSG_399);
            }

            response.setError(internalResp.getErrorCode());
            response.setError_description(internalResp.getErrorMsg());
            response.setAcquirertrxref(internalResp.getBankReference());

            boolean ok = Constants.STATUS_OK.equalsIgnoreCase(internalResp.getStatus());
            switchNotifier.notifyPayment(request.getIssuertrxref(), request.getVouchercode(),
                    Constants.INTENT_DIRECT_CASH_OUT, ok ? Constants.STATE_ACCEPTED : Constants.STATE_REJECTED);
            return ok ? ApiResult.ok(response) : ApiResult.badRequest(response);

        } catch (Exception e) {
            LOGGER.error("==Retrait Error===", e);
            return retraitError(response, Constants.ERR_CODE_399, Constants.ERR_MSG_399);
        }
    }

    private ApiResult<RetraitArgentResponse> retraitError(RetraitArgentResponse response,
                                                          String code, String description) {
        response.setError(code);
        response.setError_description(description);
        return ApiResult.badRequest(response);
    }

    private String extractOtp(RetraitArgentRequest request) {
        if (request.getAdditionaldata() == null) return null;
        for (RetraitArgentRequest.AdditionalDataEntry entry : request.getAdditionaldata()) {
            if (entry.getAdditionaldata() == null) continue;
            for (RetraitArgentRequest.OtpEntry otp : entry.getAdditionaldata()) {
                if (Constants.OTP.equalsIgnoreCase(otp.getKey())) {
                    return otp.getValue();
                }
            }
        }
        return null;
    }
}