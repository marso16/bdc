package com.capitalbanking.stage.model.bdc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Réponse du service DebitAnnulation.")
public class DebitAnnulationResponse {

    @ApiModelProperty()
    private String error;

    @ApiModelProperty()
    private String error_description;
}