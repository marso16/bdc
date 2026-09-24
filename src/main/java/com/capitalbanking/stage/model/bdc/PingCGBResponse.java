package com.capitalbanking.stage.model.bdc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "Réponse à la requête de ping. Indique si le système bancaire central est disponible pour traiter les transactions.")
public class PingCGBResponse {

    @ApiModelProperty(value = "Identifiant unique de la transaction de ping, repris depuis la requête.",
            example = "TX2025110412345678", required = true)
    private String issuertrxref;

    @ApiModelProperty(value = "Indique si le service est disponible. TRUE = disponible, FALSE = indisponible (ex. arrêt de fin de journée).",
            example = "TRUE", required = true, allowableValues = "TRUE, FALSE")
    private String available;

    @ApiModelProperty(value = "Description courte de l’erreur, vide s’il n’y a pas d’erreur")
    private String error;

    @ApiModelProperty(value = "Description longue de l’erreur, vide s’il n’y a pas d’erreur")
    private String error_description;
}