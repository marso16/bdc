package com.capitalbanking.stage.model.bdc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Réponse du service PaymentInquiry.")
public class PaymentInquiryResponse {

    @ApiModelProperty(example = "acc00000001")
    private String fromaccount;

    @ApiModelProperty(example = "0002")
    private String frommember;

    @ApiModelProperty(example = "acc00000002")
    private String accountnumber;

    @ApiModelProperty(example = "0001")
    private String tomember;

    @ApiModelProperty(example = "direct_cash_in")
    private String intent;

    @ApiModelProperty(example = "1500.0")
    private String amount;

    @ApiModelProperty(example = "174")
    private String currency;

    @ApiModelProperty(example = "ACCEPTED")
    private String state;

    @ApiModelProperty(example = "1572602202511")
    private String issuertrxref;

    @ApiModelProperty(example = "43152012")
    private String vouchercode;

    @ApiModelProperty(example = "1504613928451")
    private String createtime;

    @ApiModelProperty(example = "Desole, la limite maximum du solde du beneficiaire a été atteinte")
    private String rejectMessage;
}