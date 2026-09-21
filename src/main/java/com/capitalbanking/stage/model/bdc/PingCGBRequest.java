package com.capitalbanking.stage.model.bdc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
@Schema(description = "Ping request used to check service availability. " +
        "Should be sent before each transaction to ensure core banking is available.")
public class PingCGBRequest {

    @Schema(description = "Unique transaction identifier by API call (max 16 characters).",
            example = "TX2025110412345678", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Invalid transaction ID")
    @Size(max = 16, message = "Invalid transaction ID")
    private String issuertrxref;

    @Schema(description = "Ping message identifier. Allows tracking multiple pings.",
            example = "PING-SR-001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String msgId;

    @Schema(description = "Transaction date and time. ISO-8601 format.",
            example = "2025-11-04T10:15:30.123-03:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Invalid date")
    private String date;
}