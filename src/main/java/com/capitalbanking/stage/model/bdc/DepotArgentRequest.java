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

    @ApiModelProperty(value = "Identifiant du participant source (banque de l'agent).",
            example = "0001", required = true)
    private String frommember;

    @ApiModelProperty(value = "Numéro de compte de l'agent (débiteur).", example = "acc000000001")
    private String fromaccount;

    @ApiModelProperty(value = "Identifiant du participant destination (banque du client).",
            example = "0002", required = true)
    private String tomember;

    @ApiModelProperty(value = "Numéro de compte du client (créditeur).", example = "acc000000002", required = true)
    private String accountnumber;

    @NotBlank(message = "L'opération (intent) est obligatoire")
    @Pattern(regexp = "direct_cash_in", message = "L'opération (intent) doit être direct_cash_in")
    @ApiModelProperty(value = "Opération demandée. Valeur fixe: direct_cash_in",
            example = "direct_cash_in", required = true)
    private String intent;

    @NotBlank(message = "Le montant (amount) est obligatoire")
    @Pattern(regexp = "^\\d+(\\.\\d+)?$", message = "Le montant (amount) doit être un nombre positif valide")
    @ApiModelProperty(value = "Montant de la transaction.",
            example = "3500.0", required = true)
    private String amount;

    @NotBlank(message = "La devise (currency) est obligatoire")
    @Pattern(regexp = "^[0-9]{3}$", message = "La devise (currency) doit être un code ISO 4217 numérique à 3 chiffres")
    @ApiModelProperty(value = "Code devise ISO numérique.", example = "174", required = true)
    private String currency;

    @NotBlank(message = "La date de création (createtime) est obligatoire")
    @ApiModelProperty(value = "Horodatage de création de la transaction (epoch ms).",
            example = "1746416166000", required = true)
    private String createtime;

    @NotBlank(message = "La référence de transaction (issuertrxref) est obligatoire")
    @ApiModelProperty(value = "Référence unique de transaction émise par le Switch.",
            example = "948488604871", required = true)
    private String issuertrxref;

    @NotBlank(message = "Le code voucher (vouchercode) est obligatoire")
    @ApiModelProperty(value = "Code voucher / OTP de la transaction.", example = "1234", required = true)
    private String vouchercode;

    @ApiModelProperty(value = "Description libre de la transaction.", example = "depot d argent")
    private String description;
}