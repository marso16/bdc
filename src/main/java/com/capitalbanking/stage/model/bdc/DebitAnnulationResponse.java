package com.capitalbanking.stage.model.bdc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Réponse du service DebitAnnulation.")
public class DebitAnnulationResponse {

    @Schema()
    private String error;

    @Schema()
    private String error_description;
}