package com.capitalbanking.stage.model.ri_commons;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value = "Identifiant unique de la transaction (IN OUT).", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    @NotBlank(message = "transId ne peut pas être vide")
    private String transId;

    @ApiModelProperty(value = "refrel", example = "Jean Dupont", required = true)
    @NotBlank(message = "refrel ne peut pas être vide")
    private String refrel;

    @ApiModelProperty(value = "Date du paiement (format ISO yyyy-MM-dd).", example = "2025-10-31", required = true)
    @NotNull(message = "paymentDate est obligatoire (format yyyy-MM-dd)")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate paymentDate;

    @ApiModelProperty(value = "motif1", example = "VIR-2025-000045", required = true)
    @NotBlank(message = "motif1 ne peut pas être vide")
    private String motif1;

    @ApiModelProperty(value = "Compte débiteur du payeur (IBAN/RIB).", example = "FR7612345678901234567890185", required = true)
    @NotBlank(message = "payerAccount ne peut pas être vide")
    private String payerAccount;

    @ApiModelProperty(value = "Nom du bénéficiaire.", example = "Société ABC", required = true)
    @NotBlank(message = "benefName ne peut pas être vide")
    private String benefName;

    @ApiModelProperty(value = "Compte du bénéficiaire (IBAN/RIB).", example = "FR1420041010050500013M02606", required = true)
    @NotBlank(message = "benefAccount ne peut pas être vide")
    private String benefAccount;

    @ApiModelProperty(value = "Code BIC/SWIFT du bénéficiaire.", example = "DEUTDEFFXXX", required = true)
    @NotBlank(message = "benefBicCode ne peut pas être vide")
    private String benefBicCode;

    @ApiModelProperty(value = "Montant du virement (strictement positif).", example = "150.00", required = true)
    @NotNull(message = "amount est obligatoire")
    @DecimalMin(value = "0.01", message = "amount doit être strictement positif")
    private Double amount;

    @ApiModelProperty(value = "Devise du bénéficiaire (ISO 4217 à 3 lettres).", example = "EUR", required = true)
    @NotBlank(message = "benefCurrency ne peut pas être vide")
    @Pattern(regexp = "^[A-Z]{3}$", message = "benefCurrency doit être un code ISO à 3 lettres")
    private String benefCurrency;

    @ApiModelProperty(value = "Mode de versement (ex. VIREMENT/INTERN/EXTERN).", example = "INTERN", required = true)
    @NotBlank(message = "modev ne peut pas être vide")
    private String modev;

    @ApiModelProperty(value = "Call Source", example = "VIRINT")
    private String callSrc;
}