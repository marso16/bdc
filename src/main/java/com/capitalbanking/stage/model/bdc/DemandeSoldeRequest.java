package com.capitalbanking.stage.model.bdc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Requête du service BalanceInquiry (BCC → Participant).")
public class DemandeSoldeRequest {

    @Schema(description = "Opération demandée. Valeur fixe: acc_balance_inquiry", example = "acc_balance_inquiry", requiredMode = Schema.RequiredMode.REQUIRED)
    private String intent;

    @Schema(description = "Référence unique de transaction émise par le Switch.", example = "1572602202511", requiredMode = Schema.RequiredMode.REQUIRED)
    private String issuertrxref;

    @Schema(description = "Identifiant du participant agent.", example = "0001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String frommember;

    @Schema(description = "Horodatage de création (epoch ms).", example = "1763594514000", requiredMode = Schema.RequiredMode.REQUIRED)
    private String createtime;

    @Schema(description = "Numéro de compte de l'agent.", example = "acc000000001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fromaccount;
}