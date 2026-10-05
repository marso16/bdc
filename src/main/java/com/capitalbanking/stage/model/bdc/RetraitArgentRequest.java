package com.capitalbanking.stage.model.bdc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@ApiModel(description = "Requête du service Retrait d'argent (BCC -> Participant).")
public class RetraitArgentRequest {

    @ApiModelProperty(example = "0001", required = true)
    private String frommember;

    @ApiModelProperty(example = "acc000000001", required = true)
    private String fromaccount;

    @ApiModelProperty(example = "0002", required = true)
    private String tomember;

    private String accountnumber;

    @NotBlank(message = "L'opération (intent) est obligatoire")
    @Pattern(regexp = "direct_cash_out", message = "L'opération (intent) doit être direct_cash_out")
    @ApiModelProperty(example = "direct_cash_out", required = true)
    private String intent;

    @NotBlank(message = "Le montant (amount) est obligatoire")
    @Pattern(regexp = "^\\d+(\\.\\d+)?$", message = "Le montant (amount) doit être un nombre positif valide")
    @ApiModelProperty(example = "1250.0", required = true)
    private String amount;

    @NotBlank(message = "La devise (currency) est obligatoire")
    @Pattern(regexp = "^[0-9]{3}$", message = "La devise (currency) doit être un code ISO 4217 numérique à 3 chiffres")
    @ApiModelProperty(example = "174", required = true)
    private String currency;

    @NotBlank(message = "La date de création (createtime) est obligatoire")
    @ApiModelProperty(example = "1746416166000", required = true)
    private String createtime;

    @NotBlank(message = "La référence de transaction (issuertrxref) est obligatoire")
    @ApiModelProperty(example = "948488604872", required = true)
    private String issuertrxref;

    @NotBlank(message = "Le code voucher (vouchercode) est obligatoire")
    @ApiModelProperty(example = "123456", required = true)
    private String vouchercode;

    @ApiModelProperty(required = true)
    private List<AdditionalDataEntry> additionaldata;

    @ApiModelProperty
    private String description;

    @Getter
    @Setter
    public static class AdditionalDataEntry {
        private String iden;
        private List<OtpEntry> additionaldata;
    }

    @Getter
    @Setter
    public static class OtpEntry {
        private String key;
        private String value;
        private String encrypted;
        private String loggeable;
    }
}