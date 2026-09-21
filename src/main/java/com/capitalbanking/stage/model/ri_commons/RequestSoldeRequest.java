package com.capitalbanking.stage.model.ri_commons;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RequestSoldeRequest {

    @Schema(description = "Identifiant unique de la transaction.", example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "L'identifiant de transaction (transId) est obligatoire.")
    private String transId;

    @Schema(description = "Numéro du compte à interroger.", example = "FR7612345678901234567890185", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Le numéro de compte (accountNumber) est obligatoire.")
    private String accountNumber;
}