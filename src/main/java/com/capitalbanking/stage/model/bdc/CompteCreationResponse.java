package com.capitalbanking.stage.model.bdc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Réponse de création de compte.")
public class CompteCreationResponse {

    @ApiModelProperty(example = "00200")
    private String error;

    @ApiModelProperty(example = "Transaction acceptée")
    private String error_description;
}