package com.capitalbanking.stage.model.bdc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Réponse du service Depot d'argent.")
public class DepotArgentResponse {

    @Schema(description = "Description courte de l'erreur. Vide si succès.", example = "200")
    private String error;

    @Schema(description = "Description longue de l'erreur. Vide si succès.", example = "Transaction acceptee")
    private String error_description;

    @Schema(description = "Référence de la transaction côté Switch (reprise du request).", example = "948488604871")
    private String issuertrxref;

    @Schema(description = "Référence de la transaction côté banque participant.", example = "546387383170")
    private String acquirertrxref;
}