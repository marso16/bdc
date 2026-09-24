package com.capitalbanking.stage.model.bdc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
@ApiModel(description = "Ping request used to check service availability. " +
        "Should be sent before each transaction to ensure core banking is available.")
public class PingCGBRequest {

    @ApiModelProperty(value = "Unique transaction identifier by API call (max 16 characters).",
            example = "TX2025110412345678", required = true)
    @NotBlank(message = "Invalid transaction ID")
    @Size(max = 16, message = "Invalid transaction ID")
    private String issuertrxref;

    @ApiModelProperty(value = "Ping message identifier. Allows tracking multiple pings.",
            example = "PING-SR-001", required = true)
    private String msgId;

    @ApiModelProperty(value = "Transaction date and time. ISO-8601 format.",
            example = "2025-11-04T10:15:30.123-03:00", required = true)
    @NotBlank(message = "Invalid date")
    private String date;
}