package com.capitalbanking.stage.model.bdc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Requête du service DebitAnnulation (BCC → Participant).")
public class DebitAnnulationRequest {

    @Schema(description = "Code voucher de la transaction à annuler.", example = "20547896", requiredMode = Schema.RequiredMode.REQUIRED)
    private String vouchercode;

    @Schema(description = "Référence de transaction Switch originale.", example = "1572602202513")
    private String issuertrxref;

    @Schema(description = "Horodatage d'annulation (epoch ms).", example = "1504613929038")
    private String updatetime;

    @Schema(description = "Statut fixe: CANCELLED", example = "CANCELLED", requiredMode = Schema.RequiredMode.REQUIRED)
    private String state;

    @Schema(description = "Type d'opération: direct_cash_in ou direct_cash_out", example = "direct_cash_out", requiredMode = Schema.RequiredMode.REQUIRED)
    private String intent;
}