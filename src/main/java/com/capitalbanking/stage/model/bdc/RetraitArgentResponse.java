package com.capitalbanking.stage.model.bdc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Réponse du service Retrait d'argent.")
public class RetraitArgentResponse {

    @Schema
    private String error;

    @Schema
    private String error_description;

    @Schema(example = "948488604872")
    private String issuertrxref;

    @Schema(example = "561944928866")
    private String acquirertrxref;
}