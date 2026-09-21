package com.capitalbanking.stage.model.bdc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Réponse du service PaymentInquiry.")
public class PaymentInquiryResponse {

    @Schema(example = "acc00000001")
    private String fromaccount;

    @Schema(example = "0002")
    private String frommember;

    @Schema(example = "acc00000002")
    private String accountnumber;

    @Schema(example = "0001")
    private String tomember;

    @Schema(example = "direct_cash_in")
    private String intent;

    @Schema(example = "1500.0")
    private String amount;

    @Schema(example = "174")
    private String currency;

    @Schema(example = "ACCEPTED")
    private String state;

    @Schema(example = "1572602202511")
    private String issuertrxref;

    @Schema(example = "43152012")
    private String vouchercode;

    @Schema(example = "1504613928451")
    private String createtime;

    @Schema(example = "Desole, la limite maximum du solde du beneficiaire a ete atteinte")
    private String rejectMessage;
}