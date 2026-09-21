package com.capitalbanking.stage.model.ri_commons;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaveInternalRequestResponse {

    @Schema(description = "Identifiant unique de la transaction (IN OUT).", example = "123e4567-e89b-12d3-a456-426614174000")
    private String transId;

    @Schema(description = "Statut global du traitement.", example = "OK")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String status;

    @Schema(description = "Référence de la transaction côté système émetteur.", example = "RI-2025-000123")
    private String referenceTrans;

    @Schema(description = "Référence de l'opération retournée par la banque.", example = "BNK-REF-789654321")
    private String bankReference;

    @Schema(description = "Solde disponible du compte débité après opération.", example = "12500.45")
    private BigDecimal soldeDispDebAcc;

    @Schema(description = "Solde comptable du compte débité après opération.", example = "13000.00")
    private BigDecimal soldeCptaDebAcc;

    @Schema(description = "Code d'erreur applicatif.", example = "00")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String errorCode;

    @Schema(description = "Message d'erreur en cas d'échec.", example = "Compte introuvable.")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String errorMsg;

    @Schema(description = "Devise du compte.", example = "USD")
    private String devise;
}