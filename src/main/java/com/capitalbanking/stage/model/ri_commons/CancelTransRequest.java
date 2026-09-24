package com.capitalbanking.stage.model.ri_commons;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CancelTransRequest {

    @ApiModelProperty(value = "Identifiant unique de la transaction (IN OUT).", example = "TRX-2025-000123", required = true)
    @NotBlank(message = "transId ne peut pas être vide")
    private String transId;

    @ApiModelProperty(value = "Identifiant de l'opération d'annulation.", example = "ANNUL-987", required = true)
    @NotBlank(message = "annulTransId ne peut pas être vide")
    private String annulTransId;

    @ApiModelProperty(value = "Date  (format ISO yyyy-MM-dd).", example = "2025-11-07", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @ApiModelProperty(value = "Devise du Compte de la transaction.", example = "USD", required = true)
    private String devise;

}