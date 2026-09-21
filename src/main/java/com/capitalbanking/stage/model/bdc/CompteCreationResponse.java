package com.capitalbanking.stage.model.bdc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Réponse de création de compte.")
public class CompteCreationResponse {

    @Schema(example = "00200")
    private String error;

    @Schema(example = "Transaction acceptee")
    private String error_description;
}