package com.capitalbanking.stage.model.bdc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@Schema(description = "Requête du service Retrait d'argent (BCC -> Participant).")
public class RetraitArgentRequest {

    @NotBlank(message = "frommember is required")
    @Schema(example = "0001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String frommember;

    @NotBlank(message = "fromaccount is required")
    @Schema(example = "acc000000001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fromaccount;

    @NotBlank(message = "tomember is required")
    @Schema(example = "0002", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tomember;

    @Schema(example = "acc000000002")
    private String accountnumber;

    @NotBlank(message = "intent is required")
    @Pattern(regexp = "direct_cash_out", message = "intent must be direct_cash_out")
    @Schema(example = "direct_cash_out", requiredMode = Schema.RequiredMode.REQUIRED)
    private String intent;

    @NotBlank(message = "amount is required")
    @Pattern(regexp = "^\\d+(\\.\\d+)?$", message = "amount must be a valid positive number")
    @Schema(example = "1250.0", requiredMode = Schema.RequiredMode.REQUIRED)
    private String amount;

    @NotBlank(message = "currency is required")
    @Pattern(regexp = "^[0-9]{3}$", message = "currency must be a 3-digit numeric ISO 4217 code")
    @Schema(example = "174", requiredMode = Schema.RequiredMode.REQUIRED)
    private String currency;

    @NotBlank(message = "createtime is required")
    @Schema(example = "1746416166000", requiredMode = Schema.RequiredMode.REQUIRED)
    private String createtime;

    @NotBlank(message = "issuertrxref is required")
    @Schema(example = "948488604872", requiredMode = Schema.RequiredMode.REQUIRED)
    private String issuertrxref;

    @NotBlank(message = "vouchercode is required")
    @Schema(example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String vouchercode;

    @NotNull(message = "additionaldata is required")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private List<AdditionalDataEntry> additionaldata;

    @Schema
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