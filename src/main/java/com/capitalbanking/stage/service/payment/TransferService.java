package com.capitalbanking.stage.service.payment;

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
import com.capitalbanking.stage.shared.ApiResult;
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
            Object[] deviseData = coreRepository.getDevRef();

            String devRef = deviseData[0].toString();
            String devIson = deviseData[1].toString();

            if (request.getCurrency() == null || request.getCurrency().isEmpty() || !request.getCurrency().equals(devIson)) {
                return depotError(response, Constants.ERR_CODE_399,
                        "La devise doit être exclusivement : " + devIson + " (" + devRef + ")");
            }

            if (!bankCodbnq.equals(request.getTomember())) {
                return depotError(response, Constants.ERR_CODE_399,
                        "Le participant destinataire (tomember) doit être le code de notre banque pour un dépôt");
            }

            String fromAccount = (request.getFromaccount() == null || request.getFromaccount().trim().isEmpty())
                    ? null : request.getFromaccount().trim();

            boolean isExternalMember = !bankCodbnq.equals(request.getFrommember());

            if (isExternalMember && fromAccount != null) {
                return depotError(response, Constants.ERR_CODE_399,
                        "Le compte émetteur (fromaccount) doit être vide lorsque le participant émetteur (frommember) est externe");
            }

            if (fromAccount == null) {
                if (isExternalMember) {
                    fromAccount = coreRepository.getPoolAccount("DEPOT");
                    LOGGER.info("depot: fromaccount is blank and frommember is external -> using pool account: {}", fromAccount);
                    if (fromAccount == null) {
                        return depotError(response, Constants.ERR_CODE_399,
                                "Aucun compte de pool n'est configuré pour le dépôt (paramétrage fx5y8)");
                    }
                } else {
                    return depotError(response, Constants.ERR_CODE_304,
                            "Le compte émetteur (fromaccount) est obligatoire lorsque le participant émetteur (frommember) est notre banque");
                }
            }

            String accountNumber = request.getAccountnumber() != null ? request.getAccountnumber().trim() : "";
            if (accountNumber.isEmpty()) {
                return depotError(response, Constants.ERR_CODE_304,
                        "Le compte bénéficiaire (accountnumber) est obligatoire pour un dépôt");
            }

            if (fromAccount.equals(accountNumber)) {
                return depotError(response, Constants.ERR_CODE_300, Constants.ERR_MSG_300);
            }

            Compte payerCompte = compteService.findCompteByCompte(fromAccount);
            if (payerCompte == null) {
                return depotError(response, Constants.ERR_CODE_301,
                        "Compte émetteur introuvable : " + fromAccount);
            }

            Compte benefCompte = compteService.findCompteByCompte(accountNumber);
            if (benefCompte == null) {
                return depotError(response, Constants.ERR_CODE_301,
                        "Compte bénéficiaire introuvable : " + accountNumber);
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
                    .benefAccount(accountNumber)
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

            boolean ok = Constants.STATUS_OK.equalsIgnoreCase(internalResp.getStatus());
            response.setError(ok ? Constants.BCC_SUCCESS_CODE : internalResp.getErrorCode());
            response.setError_description(ok ? Constants.BCC_SUCCESS_MSG : internalResp.getErrorMsg());
            response.setAcquirertrxref(internalResp.getBankReference());

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
            Object[] deviseData = coreRepository.getDevRef();

            String devRef = deviseData[0].toString();
            String devIson = deviseData[1].toString();

            if (request.getCurrency() == null || request.getCurrency().isEmpty() || !request.getCurrency().equals(devIson)) {
                return retraitError(response, Constants.ERR_CODE_399,
                        "La devise doit être exclusivement : " + devIson + " (" + devRef + ")");
            }

            if (!bankCodbnq.equals(request.getFrommember())) {
                return retraitError(response, Constants.ERR_CODE_399,
                        "Le participant émetteur (frommember) '" + request.getFrommember()
                                + "' n'est pas valide : un retrait doit toujours provenir d'un compte de notre banque");
            }

            if (request.getFromaccount() == null || request.getFromaccount().trim().isEmpty()) {
                return retraitError(response, Constants.ERR_CODE_304,
                        "Le compte émetteur (fromaccount) est obligatoire pour un retrait");
            }

            String accountNumber = (request.getAccountnumber() == null || request.getAccountnumber().trim().isEmpty())
                    ? null : request.getAccountnumber().trim();

            boolean isExternalTomember = !bankCodbnq.equals(request.getTomember());

            if (isExternalTomember && accountNumber != null) {
                return retraitError(response, Constants.ERR_CODE_399,
                        "Le compte bénéficiaire (accountnumber) doit être vide " +
                                "lorsque le participant destinataire (tomember) est externe");
            }

            if (accountNumber == null) {
                if (isExternalTomember) {
                    accountNumber = coreRepository.getPoolAccount("RETRAIT");
                    LOGGER.info("retrait: accountnumber is blank and tomember is external -> using pool account: {}", accountNumber);
                    if (accountNumber == null) {
                        return retraitError(response, Constants.ERR_CODE_399,
                                "Aucun compte de pool n'est configuré pour le retrait (paramétrage fx5y8)");
                    }
                } else {
                    return retraitError(response, Constants.ERR_CODE_304,
                            "Le compte bénéficiaire (accountnumber) est " +
                                    "obligatoire lorsque le participant destinataire (tomember) est notre banque");
                }
            }

            String otp = extractOtp(request);
            if (otp == null || otp.trim().isEmpty()) {
                return retraitError(response, Constants.ERR_CODE_399,
                        "Le code OTP est obligatoire dans additionaldata");
            }

            if (request.getFromaccount().trim().equals(accountNumber)) {
                return retraitError(response, Constants.ERR_CODE_300, Constants.ERR_MSG_300);
            }

            Compte payerCompte = compteService.findCompteByCompte(request.getFromaccount().trim());
            if (payerCompte == null) {
                return retraitError(response, Constants.ERR_CODE_301,
                        "Compte client introuvable : " + request.getFromaccount());
            }

            Compte agentCompte = compteService.findCompteByCompte(accountNumber);
            if (agentCompte == null) {
                return retraitError(response, Constants.ERR_CODE_301,
                        "Compte agent introuvable : " + accountNumber);
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

            boolean ok = Constants.STATUS_OK.equalsIgnoreCase(internalResp.getStatus());
            response.setError(ok ? Constants.BCC_SUCCESS_CODE : internalResp.getErrorCode());
            response.setError_description(ok ? Constants.BCC_SUCCESS_MSG : internalResp.getErrorMsg());
            response.setAcquirertrxref(internalResp.getBankReference());

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