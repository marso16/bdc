package com.capitalbanking.stage.model.ri_commons;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateAccountResponse {
    private String accountNumber;
    private String clientId;
    private String status;
    private String errorCode;
    private String errorMsg;
}