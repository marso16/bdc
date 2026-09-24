package com.capitalbanking.stage.model.bdc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ApiModel(description = "Réponse du service BalanceInquiry.")
public class DemandeSoldeResponse {

    @ApiModelProperty(example = "200")
    private String error;

    @ApiModelProperty(example = "Transaction acceptee")
    private String error_description;

    @ApiModelProperty(example = "1572602202511")
    private String issuertrxref;

    @ApiModelProperty(example = "542420230823")
    private String acquirertrxref;

    private List<SrcAccount> srcaccounts;

    @Getter
    @Setter
    public static class SrcAccount {
        private String iden;
        private String type;
        private List<Balance> balances;
    }

    @Getter
    @Setter
    public static class Balance {
        @ApiModelProperty(value = "\"02\" = available balance")
        private String amountType;
        private String currency;
        private BigDecimal amount;
    }
}