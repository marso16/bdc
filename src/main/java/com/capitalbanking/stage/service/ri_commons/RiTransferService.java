package com.capitalbanking.stage.service.ri_commons;

import com.capitalbanking.stage.model.ri_commons.*;
import com.capitalbanking.stage.repository.ri_commons.RiTransferReqDao;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.Valid;

@Service
@Validated
public class RiTransferService {

    private final RiTransferReqDao dao;

    public RiTransferService(RiTransferReqDao dao) {
        this.dao = dao;
    }

    @Transactional
    public SaveInternalRequestResponse saveInternalRequest(SaveInternalRequestRequest req) {
        return dao.saveInternalRequest(req);
    }

    public RequestSoldeResponse requestSoldeNoFee(RequestSoldeRequest r) {
        return dao.requestSolde(r.getTransId(), r.getAccountNumber(), null, "N", null, null);
    }

    public String getTransactionStatus(String transId) {
        return dao.getTransactionStatus(transId);
    }

    public CancelTransResult cancelTrans(@Valid CancelTransRequest request) throws Exception {
        return dao.cancelTrans(request);
    }

    @Transactional
    public CreateAccountResponse createAccount(CreateAccountRequest req) {
        return dao.createAccount(req);
    }
}
