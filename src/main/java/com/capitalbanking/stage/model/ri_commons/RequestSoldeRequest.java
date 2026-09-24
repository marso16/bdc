package com.capitalbanking.stage.model.ri_commons;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RequestSoldeRequest {

    @ApiModelProperty(value = "Identifiant unique de la transaction.", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    @NotBlank(message = "L'identifiant de transaction (transId) est obligatoire.")
    private String transId;

    @ApiModelProperty(value = "Numéro du compte à interroger.", example = "FR7612345678901234567890185", required = true)
    @NotBlank(message = "Le numéro de compte (accountNumber) est obligatoire.")
    private String accountNumber;
}