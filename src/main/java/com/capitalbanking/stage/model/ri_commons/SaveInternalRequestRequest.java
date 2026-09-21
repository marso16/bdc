package com.capitalbanking.stage.model.ri_commons;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SaveInternalRequestRequest {

    @Schema(description = "Identifiant unique de la transaction (IN OUT).", example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "transId ne peut pas être vide")
    private String transId;

    @Schema(description = "refrel", example = "Jean Dupont", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "refrel ne peut pas être vide")
    private String refrel;

    @Schema(description = "Date du paiement (format ISO yyyy-MM-dd).", example = "2025-10-31", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "paymentDate est obligatoire (format yyyy-MM-dd)")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate paymentDate;

    @Schema(description = "motif1", example = "VIR-2025-000045", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "motif1 ne peut pas être vide")
    private String motif1;

    @Schema(description = "Compte débiteur du payeur (IBAN/RIB).", example = "FR7612345678901234567890185", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "payerAccount ne peut pas être vide")
    private String payerAccount;

    @Schema(description = "Nom du bénéficiaire.", example = "Société ABC", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "benefName ne peut pas être vide")
    private String benefName;

    @Schema(description = "Compte du bénéficiaire (IBAN/RIB).", example = "FR1420041010050500013M02606", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "benefAccount ne peut pas être vide")
    private String benefAccount;

    @Schema(description = "Code BIC/SWIFT du bénéficiaire.", example = "DEUTDEFFXXX", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "benefBicCode ne peut pas être vide")
    private String benefBicCode;

    @Schema(description = "Montant du virement (strictement positif).", example = "150.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "amount est obligatoire")
    @DecimalMin(value = "0.01", message = "amount doit être strictement positif")
    private Double amount;

    @Schema(description = "Devise du bénéficiaire (ISO 4217 à 3 lettres).", example = "EUR", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "benefCurrency ne peut pas être vide")
    @Pattern(regexp = "^[A-Z]{3}$", message = "benefCurrency doit être un code ISO à 3 lettres")
    private String benefCurrency;

    @Schema(description = "Mode de versement (ex. VIREMENT/INTERN/EXTERN).", example = "INTERN", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "modev ne peut pas être vide")
    private String modev;

    @Schema(description = "Call Source", example = "VIRINT")
    private String callSrc;
}