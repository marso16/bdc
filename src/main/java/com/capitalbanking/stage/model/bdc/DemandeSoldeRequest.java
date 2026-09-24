package com.capitalbanking.stage.model.bdc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Requête du service BalanceInquiry (BCC -> Participant).")
public class DemandeSoldeRequest {

    @ApiModelProperty(value = "Opération demandée. Valeur fixe: acc_balance_inquiry",
            example = "acc_balance_inquiry", required = true)
    private String intent;

    @ApiModelProperty(value = "Référence unique de transaction émise par le Switch.",
            example = "1572602202511", required = true)
    private String issuertrxref;

    @ApiModelProperty(value = "Identifiant du participant agent.", example = "0001", required = true)
    private String frommember;

    @ApiModelProperty(value = "Horodatage de création (epoch ms).", example = "1763594514000", required = true)
    private String createtime;

    @ApiModelProperty(value = "Numéro de compte de l'agent.", example = "acc000000001", required = true)
    private String fromaccount;
}