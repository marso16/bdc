package com.capitalbanking.stage.model.ri_commons;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaveInternalRequestResponse {

    @ApiModelProperty(value = "Identifiant unique de la transaction (IN OUT).", example = "123e4567-e89b-12d3-a456-426614174000")
    private String transId;

    @ApiModelProperty(value = "Statut global du traitement.", example = "OK")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String status;

    @ApiModelProperty(value = "Référence de la transaction côté système émetteur.", example = "RI-2025-000123")
    private String referenceTrans;

    @ApiModelProperty(value = "Référence de l'opération retournée par la banque.", example = "BNK-REF-789654321")
    private String bankReference;

    @ApiModelProperty(value = "Solde disponible du compte débité après opération.", example = "12500.45")
    private BigDecimal soldeDispDebAcc;

    @ApiModelProperty(value = "Solde comptable du compte débité après opération.", example = "13000.00")
    private BigDecimal soldeCptaDebAcc;

    @ApiModelProperty(value = "Code d'erreur applicatif.", example = "00")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String errorCode;

    @ApiModelProperty(value = "Message d'erreur en cas d'échec.", example = "Compte introuvable.")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String errorMsg;

    @ApiModelProperty(value = "Devise du compte.", example = "USD")
    private String devise;
}