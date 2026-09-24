package com.capitalbanking.stage.model.bdc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Réponse du service Retrait d'argent.")
public class RetraitArgentResponse {

    @ApiModelProperty
    private String error;

    @ApiModelProperty
    private String error_description;

    @ApiModelProperty(example = "948488604872")
    private String issuertrxref;

    @ApiModelProperty(example = "561944928866")
    private String acquirertrxref;
}