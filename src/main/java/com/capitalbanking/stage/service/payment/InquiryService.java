package com.capitalbanking.stage.service.payment;

import com.capitalbanking.stage.shared.ApiResult;
import com.capitalbanking.stage.model.bdc.*;
import com.capitalbanking.stage.model.core.Compte;
import com.capitalbanking.stage.model.ri_commons.RequestSoldeRequest;
import com.capitalbanking.stage.model.ri_commons.RequestSoldeResponse;
import com.capitalbanking.stage.repository.core.CompteRepository;
import com.capitalbanking.stage.service.core.CompteService;
import com.capitalbanking.stage.service.ri_commons.RiTransferService;
import com.capitalbanking.stage.shared.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class InquiryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InquiryService.class);

    private final CompteService compteService;
    private final CompteRepository compteRepository;
    private final RiTransferService riTransferService;
    private final ObjectMapper objectMapper;

    public InquiryService(CompteService compteService,
                          CompteRepository compteRepository,
                          RiTransferService riTransferService,
                          ObjectMapper objectMapper) {
        this.compteService = compteService;
        this.compteRepository = compteRepository;
        this.riTransferService = riTransferService;
        this.objectMapper = objectMapper;
    }

    // ----------------------------------------------------- payment inquiry
    public ApiResult<PaymentInquiryResponse> paymentInquiry(PaymentInquiryRequest request) {
        try {
            if (request.getAccountid() == null || request.getAccountid().trim().isEmpty()) {
                PaymentInquiryResponse err = new PaymentInquiryResponse();
                err.setState(Constants.STATUS_REJECTED);
                err.setRejectMessage(Constants.ERR_MSG_301);
                return ApiResult.badRequest(err);
            }

            String transId = (request.getPaymentreference() != null
                    && !request.getPaymentreference().trim().isEmpty())
                    ? request.getPaymentreference()
                    : request.getAccountid();

            LOGGER.info("paymentInquiry -> getTransactionStatus: transId={}", transId);

            String json = riTransferService.getTransactionStatus(transId);

            if (json == null || json.trim().isEmpty()) {
                PaymentInquiryResponse err = new PaymentInquiryResponse();
                err.setState(Constants.STATUS_PENDING);
                err.setRejectMessage("Transaction not found");
                return ApiResult.badRequest(err);
            }

            JsonNode node = objectMapper.readTree(json);

            PaymentInquiryResponse response = new PaymentInquiryResponse();
            response.setIssuertrxref(transId);
            response.setFromaccount(request.getAccountid());

            String status = node.has("status") ? node.get("status").asText() : "";
            String errCode = node.has("errorCode") ? node.get("errorCode").asText() : "";

            if (Constants.STATUS_OK.equalsIgnoreCase(status) || Constants.ERR_CODE_200.equals(errCode)) {
                response.setState(Constants.STATUS_ACCEPTED);
            } else if (Constants.STATUS_KO.equalsIgnoreCase(status)) {
                response.setState(Constants.STATUS_REJECTED);
                response.setRejectMessage(node.has("errorMsg") ? node.get("errorMsg").asText() : "");
            } else {
                response.setState(Constants.STATUS_PENDING);
            }

            if (node.has("nooper")) response.setVouchercode(node.get("nooper").asText());
            if (node.has("createdAt")) response.setCreatetime(node.get("createdAt").asText());

            return ApiResult.ok(response);

        } catch (Exception e) {
            LOGGER.error("==paymentInquiry Error===", e);
            PaymentInquiryResponse err = new PaymentInquiryResponse();
            err.setState(Constants.STATUS_REJECTED);
            err.setRejectMessage(Constants.ERR_MSG_399);
            return ApiResult.badRequest(err);
        }
    }

    // ----------------------------------------------------- balance inquiry
    public ApiResult<DemandeSoldeResponse> balanceInquiry(DemandeSoldeRequest request) {
        DemandeSoldeResponse response = new DemandeSoldeResponse();
        response.setIssuertrxref(request.getIssuertrxref());

        try {
            if (request.getFromaccount() == null || request.getFromaccount().trim().isEmpty()) {
                response.setError(Constants.ERR_CODE_304);
                response.setError_description(Constants.ERR_MSG_304);
                return ApiResult.badRequest(response);
            }

            RequestSoldeRequest soldeReq = new RequestSoldeRequest();
            soldeReq.setTransId(request.getIssuertrxref());
            soldeReq.setAccountNumber(request.getFromaccount());

            RequestSoldeResponse soldeResp = riTransferService.requestSoldeNoFee(soldeReq);

            if (soldeResp == null) {
                LOGGER.error("requestSoldeNoFee returned null for transId={}", request.getIssuertrxref());
                response.setError(Constants.ERR_CODE_399);
                response.setError_description(Constants.ERR_MSG_399);
                return ApiResult.badRequest(response);
            }

            response.setError(soldeResp.getErrorCode());
            response.setError_description(soldeResp.getErrorMsg());
            response.setAcquirertrxref(soldeResp.getBankReference());

            if (Constants.STATUS_OK.equalsIgnoreCase(soldeResp.getStatus())) {
                DemandeSoldeResponse.Balance balance = new DemandeSoldeResponse.Balance();
                balance.setAmountType("02");
                balance.setCurrency(soldeResp.getDevise());
                balance.setAmount(soldeResp.getSoldeDisp() != null
                        ? new BigDecimal(soldeResp.getSoldeDisp()) : BigDecimal.ZERO);

                DemandeSoldeResponse.SrcAccount account = new DemandeSoldeResponse.SrcAccount();
                account.setIden(request.getFromaccount());
                account.setType("ACCOUNT");
                account.setBalances(Collections.singletonList(balance));

                response.setSrcaccounts(Collections.singletonList(account));
                return ApiResult.ok(response);
            } else {
                return ApiResult.badRequest(response);
            }

        } catch (Exception e) {
            LOGGER.error("==balanceInquiry Error===", e);
            response.setError(Constants.ERR_CODE_399);
            response.setError_description(Constants.ERR_MSG_399);
            return ApiResult.badRequest(response);
        }
    }

    // ----------------------------------------------------- account inquiry
    public ApiResult<AccountInquiryResponse> accountInquiry(AccountInquiryRequest request) {
        AccountInquiryResponse response = new AccountInquiryResponse();

        try {
            if (request.getDstaccounts() == null || request.getDstaccounts().isEmpty()) {
                return accountInquiryError(response, Constants.ERR_CODE_304, Constants.ERR_MSG_304);
            }

            String accountNumber = request.getDstaccounts().get(0).getIden();
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                return accountInquiryError(response, Constants.ERR_CODE_304, Constants.ERR_MSG_304);
            }

            accountNumber = accountNumber.trim();

            Compte compte = compteService.findCompteByCompte(accountNumber);
            if (compte == null) {
                return accountInquiryError(response, Constants.ERR_CODE_301, Constants.ERR_MSG_301);
            }

            List<Object[]> results = compteRepository.findCustomerDataByAccount(accountNumber);

            if (results == null || results.isEmpty()) {
                LOGGER.warn("No idp record found for account: {}", accountNumber);
                response.setError("");
                response.setError_description("");
                response.setState(Constants.STATUS_ACCEPTED);
                response.setAcquirertrxref(accountNumber);
                response.setReceivercustomerdata(new AccountInquiryResponse.ReceiverCustomerData());
                return ApiResult.ok(response);
            }

            Object[] row = results.get(0);

            AccountInquiryResponse.ReceiverCustomerData customerData =
                    new AccountInquiryResponse.ReceiverCustomerData(
                            str(row[0]),  // firstname
                            str(row[1]),  // secondname
                            str(row[2]),  // middlename
                            str(row[3]),  // surname
                            str(row[4]),  // name
                            str(row[5]),  // idtype
                            str(row[6]),  // idnumber
                            str(row[7]),  // address
                            str(row[8]),  // city
                            str(row[9]),  // country
                            str(row[10]), // phone
                            str(row[11]), // email
                            str(row[12]), // gender
                            str(row[13])  // birthdate
                    );

            response.setError("");
            response.setError_description("");
            response.setState(Constants.STATUS_ACCEPTED);
            response.setAcquirertrxref(accountNumber);
            response.setReceivercustomerdata(customerData);

            return ApiResult.ok(response);

        } catch (Exception e) {
            LOGGER.error("==accountInquiry Error===", e);
            response.setError(Constants.ERR_CODE_399);
            response.setError_description(Constants.ERR_MSG_399);
            response.setState(Constants.STATUS_REJECTED);
            return ApiResult.badRequest(response);
        }
    }

    private ApiResult<AccountInquiryResponse> accountInquiryError(AccountInquiryResponse response,
                                                                  String code, String description) {
        response.setError(code);
        response.setError_description(description);
        response.setState(Constants.STATUS_REJECTED);
        return ApiResult.badRequest(response);
    }

    private String str(Object o) {
        return o != null ? o.toString().trim() : null;
    }
}