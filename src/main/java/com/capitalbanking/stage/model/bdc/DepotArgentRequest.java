package com.capitalbanking.stage.model.bdc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Schema(description = "Requête du service Depot d'argent (BCC → Participant).")
public class DepotArgentRequest {

    @NotBlank(message = "frommember is required")
    @Schema(description = "Identifiant du participant source (banque de l'agent).", example = "0001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String frommember;

    @Schema(description = "Numéro de compte de l'agent (débiteur).", example = "acc000000001")
    private String fromaccount;

    @NotBlank(message = "tomember is required")
    @Schema(description = "Identifiant du participant destination (banque du client).", example = "0002", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tomember;

    @NotBlank(message = "accountnumber is required")
    @Schema(description = "Numéro de compte du client (créditeur).", example = "acc000000002", requiredMode = Schema.RequiredMode.REQUIRED)
    private String accountnumber;

    @NotBlank(message = "intent is required")
    @Pattern(regexp = "direct_cash_in", message = "intent must be direct_cash_in")
    @Schema(description = "Opération demandée. Valeur fixe: direct_cash_in", example = "direct_cash_in", requiredMode = Schema.RequiredMode.REQUIRED)
    private String intent;

    @NotBlank(message = "amount is required")
    @Pattern(regexp = "^\\d+(\\.\\d+)?$", message = "amount must be a valid positive number")
    @Schema(description = "Montant de la transaction.", example = "3500.0", requiredMode = Schema.RequiredMode.REQUIRED)
    private String amount;

    @NotBlank(message = "currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "currency must be a 3-letter ISO code")
    @Schema(description = "Code devise ISO.", example = "EUR", requiredMode = Schema.RequiredMode.REQUIRED)
    private String currency;

    @NotBlank(message = "createtime is required")
    @Schema(description = "Horodatage de création de la transaction (epoch ms).", example = "1746416166000", requiredMode = Schema.RequiredMode.REQUIRED)
    private String createtime;

    @NotBlank(message = "issuertrxref is required")
    @Schema(description = "Référence unique de transaction émise par le Switch.", example = "948488604871", requiredMode = Schema.RequiredMode.REQUIRED)
    private String issuertrxref;

    @NotBlank(message = "vouchercode is required")
    @Schema(description = "Code voucher / OTP de la transaction.", example = "1234", requiredMode = Schema.RequiredMode.REQUIRED)
    private String vouchercode;

    @Schema(description = "Description libre de la transaction.", example = "depot d argent")
    private String description;
}