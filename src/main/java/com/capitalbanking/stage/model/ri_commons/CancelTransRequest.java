package com.capitalbanking.stage.model.ri_commons;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Identifiant unique de la transaction (IN OUT).", example = "TRX-2025-000123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "transId ne peut pas être vide")
    private String transId;

    @Schema(description = "Identifiant de l'opération d'annulation.", example = "ANNUL-987", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "annulTransId ne peut pas être vide")
    private String annulTransId;

    @Schema(description = "Date  (format ISO yyyy-MM-dd).", example = "2025-11-07", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Schema(description = "Devise du Compte de la transaction.", example = "USD", requiredMode = Schema.RequiredMode.REQUIRED)
    private String devise;

}