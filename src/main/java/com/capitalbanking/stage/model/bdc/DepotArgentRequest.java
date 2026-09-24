package com.capitalbanking.stage.model.bdc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@ApiModel(description = "Requête du service Depot d'argent (BCC -> Participant).")
public class DepotArgentRequest {

    @NotBlank(message = "frommember is required")
    @ApiModelProperty(value = "Identifiant du participant source (banque de l'agent).",
            example = "0001", required = true)
    private String frommember;

    @ApiModelProperty(value = "Numéro de compte de l'agent (débiteur).", example = "acc000000001")
    private String fromaccount;

    @NotBlank(message = "tomember is required")
    @ApiModelProperty(value = "Identifiant du participant destination (banque du client).",
            example = "0002", required = true)
    private String tomember;

    @NotBlank(message = "accountnumber is required")
    @ApiModelProperty(value = "Numéro de compte du client (créditeur).", example = "acc000000002", required = true)
    private String accountnumber;

    @NotBlank(message = "intent is required")
    @Pattern(regexp = "direct_cash_in", message = "intent must be direct_cash_in")
    @ApiModelProperty(value = "Opération demandée. Valeur fixe: direct_cash_in",
            example = "direct_cash_in", required = true)
    private String intent;

    @NotBlank(message = "amount is required")
    @Pattern(regexp = "^\\d+(\\.\\d+)?$", message = "amount must be a valid positive number")
    @ApiModelProperty(value = "Montant de la transaction.",
            example = "3500.0", required = true)
    private String amount;

    @NotBlank(message = "currency is required")
    @Pattern(regexp = "^[0-9]{3}$", message = "currency must be a 3-digit numeric ISO 4217 code")
    @ApiModelProperty(value = "Code devise ISO numérique.", example = "174", required = true)
    private String currency;

    @NotBlank(message = "createtime is required")
    @ApiModelProperty(value = "Horodatage de création de la transaction (epoch ms).",
            example = "1746416166000", required = true)
    private String createtime;

    @NotBlank(message = "issuertrxref is required")
    @ApiModelProperty(value = "Référence unique de transaction émise par le Switch.",
            example = "948488604871", required = true)
    private String issuertrxref;

    @NotBlank(message = "vouchercode is required")
    @ApiModelProperty(value = "Code voucher / OTP de la transaction.", example = "1234", required = true)
    private String vouchercode;

    @ApiModelProperty(value = "Description libre de la transaction.", example = "depot d argent")
    private String description;
}