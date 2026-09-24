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

    @NotBlank(message = "frommember is required")
    @ApiModelProperty(example = "0001", required = true)
    private String frommember;

    @NotBlank(message = "fromaccount is required")
    @ApiModelProperty(example = "acc000000001", required = true)
    private String fromaccount;

    @NotBlank(message = "tomember is required")
    @ApiModelProperty(example = "0002", required = true)
    private String tomember;

    @ApiModelProperty(example = "acc000000002")
    private String accountnumber;

    @NotBlank(message = "intent is required")
    @Pattern(regexp = "direct_cash_out", message = "intent must be direct_cash_out")
    @ApiModelProperty(example = "direct_cash_out", required = true)
    private String intent;

    @NotBlank(message = "amount is required")
    @Pattern(regexp = "^\\d+(\\.\\d+)?$", message = "amount must be a valid positive number")
    @ApiModelProperty(example = "1250.0", required = true)
    private String amount;

    @NotBlank(message = "currency is required")
    @Pattern(regexp = "^[0-9]{3}$", message = "currency must be a 3-digit numeric ISO 4217 code")
    @ApiModelProperty(example = "174", required = true)
    private String currency;

    @NotBlank(message = "createtime is required")
    @ApiModelProperty(example = "1746416166000", required = true)
    private String createtime;

    @NotBlank(message = "issuertrxref is required")
    @ApiModelProperty(example = "948488604872", required = true)
    private String issuertrxref;

    @NotBlank(message = "vouchercode is required")
    @ApiModelProperty(example = "123456", required = true)
    private String vouchercode;

    @NotNull(message = "additionaldata is required")
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