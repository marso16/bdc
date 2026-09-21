package com.capitalbanking.stage.model.ri_commons;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestSoldeResponse {

    @Schema(description = "Identifiant unique de la transaction.", example = "123e4567-e89b-12d3-a456-426614174000")
    private String transId;

    @Schema(description = "Numéro du compte interrogé.", example = "FR7612345678901234567890185")
    private String accountNumber;

    @Schema(description = "Solde disponible du compte", example = "1250.45")
    private String soldeDisp;

    @Schema(description = "Code(s) d'erreur applicatif.", example = "301-302")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String errorCode;

    @Schema(description = "Message d'erreur lisible en cas d’échec.", example = "Compte introuvable.")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String errorMsg;

    @Schema(description = "Référence bancaire générée.", example = "A110110")
    private String bankReference;

    @Schema(description = "Statut global de la requête.", example = "OK")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String status;

    @Schema(description = "Devise du compte", example = "USD")
    private String devise;
}