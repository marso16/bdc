package com.capitalbanking.stage.model.bdc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Réponse du service Depot d'argent.")
public class DepotArgentResponse {

    @ApiModelProperty(value = "Description courte de l'erreur. Vide si succès.", example = "200")
    private String error;

    @ApiModelProperty(value = "Description longue de l'erreur. Vide si succès.", example = "Transaction acceptee")
    private String error_description;

    @ApiModelProperty(value = "Référence de la transaction côté Switch (reprise du request).", example = "948488604871")
    private String issuertrxref;

    @ApiModelProperty(value = "Référence de la transaction côté banque participant.", example = "546387383170")
    private String acquirertrxref;
}