package com.capitalbanking.stage.model.bdc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Réponse à la requête de ping. Indique si le système bancaire central est disponible pour traiter les transactions.")
public class PingCGBResponse {

    @Schema(description = "Identifiant unique de la transaction de ping, repris depuis la requête.",
            example = "TX2025110412345678", requiredMode = Schema.RequiredMode.REQUIRED)
    private String issuertrxref;

    @Schema(description = "Indique si le service est disponible. TRUE = disponible, FALSE = indisponible (ex. arrêt de fin de journée).",
            example = "TRUE", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"TRUE", "FALSE"})
    private String available;

    @Schema(description = "Description courte de l’erreur, vide s’il n’y a pas d’erreur")
    private String error;

    @Schema(description = "Description longue de l’erreur, vide s’il n’y a pas d’erreur")
    private String error_description;
}