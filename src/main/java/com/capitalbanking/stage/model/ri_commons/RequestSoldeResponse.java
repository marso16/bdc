package com.capitalbanking.stage.model.ri_commons;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestSoldeResponse {

    @ApiModelProperty(value = "Identifiant unique de la transaction.", example = "123e4567-e89b-12d3-a456-426614174000")
    private String transId;

    @ApiModelProperty(value = "Numéro du compte interrogé.", example = "FR7612345678901234567890185")
    private String accountNumber;

    @ApiModelProperty(value = "Solde disponible du compte", example = "1250.45")
    private String soldeDisp;

    @ApiModelProperty(value = "Code(s) d'erreur applicatif.", example = "301-302")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String errorCode;

    @ApiModelProperty(value = "Message d'erreur lisible en cas d’échec.", example = "Compte introuvable.")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String errorMsg;

    @ApiModelProperty(value = "Référence bancaire générée.", example = "A110110")
    private String bankReference;

    @ApiModelProperty(value = "Statut global de la requête.", example = "OK")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String status;

    @ApiModelProperty(value = "Devise du compte", example = "USD")
    private String devise;
}