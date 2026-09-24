package com.capitalbanking.stage.model.bdc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Requête du service DebitAnnulation (BCC -> Participant).")
public class DebitAnnulationRequest {

    @ApiModelProperty(value = "Code voucher de la transaction à annuler.",
            example = "20547896", required = true)
    private String vouchercode;

    @ApiModelProperty(value = "Référence de transaction Switch originale.", example = "1572602202513")
    private String issuertrxref;

    @ApiModelProperty(value = "Horodatage d'annulation (epoch ms).", example = "1504613929038")
    private String updatetime;

    @ApiModelProperty(value = "Statut fixe: CANCELLED", example = "CANCELLED", required = true)
    private String state;

    @ApiModelProperty(value = "Type d'opération: direct_cash_in ou direct_cash_out",
            example = "direct_cash_out", required = true)
    private String intent;
}