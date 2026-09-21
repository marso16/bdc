package com.capitalbanking.stage.model.bdc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Schema(description = "Réponse du service BalanceInquiry.")
public class DemandeSoldeResponse {

    @Schema(example = "200")
    private String error;

    @Schema(example = "Transaction acceptee")
    private String error_description;

    @Schema(example = "1572602202511")
    private String issuertrxref;

    @Schema(example = "542420230823")
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
        @Schema(description = "\"02\" = available balance")
        private String amountType;
        private String currency;
        private BigDecimal amount;
    }
}