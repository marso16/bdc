package com.capitalbanking.stage.model.bdc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Schema(description = "Requête de création de compte (BCC → Participant).")
public class CompteCreationRequest {

    @NotBlank(message = "requestID is required")
    @Schema(example = "FNB1235D4635", requiredMode = Schema.RequiredMode.REQUIRED)
    private String requestID;

    @NotBlank(message = "bank is required")
    @Schema(example = "bank001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String bank;

    @NotBlank(message = "firstName is required")
    @Schema(example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    @Schema(example = "William")
    private String middleName;

    @Schema
    private String surname;

    @NotBlank(message = "lastName is required")
    @Schema(example = "Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @NotBlank(message = "gender is required")
    @Pattern(regexp = "^[MF]$", message = "gender must be M or F")
    @Schema(example = "M", requiredMode = Schema.RequiredMode.REQUIRED)
    private String gender;

    @NotBlank(message = "birthdate is required")
    @Schema(example = "794837754346", requiredMode = Schema.RequiredMode.REQUIRED)
    private String birthdate;

    @NotBlank(message = "gsm is required")
    @Pattern(regexp = "^[0-9]+$", message = "gsm must be numeric")
    @Schema(example = "123456789", requiredMode = Schema.RequiredMode.REQUIRED)
    private String gsm;

    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    @Schema(example = "johndoe@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "address is required")
    @Schema(example = "23 Rue de la Santé", requiredMode = Schema.RequiredMode.REQUIRED)
    private String address;

    @NotBlank(message = "nationality is required")
    @Schema(example = "COM", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nationality;

    @NotBlank(message = "country is required")
    @Schema(example = "COM", requiredMode = Schema.RequiredMode.REQUIRED)
    private String country;

    @Schema(example = "REGION MORONI")
    private String region;

    @NotBlank(message = "city is required")
    @Schema(example = "MORONI", requiredMode = Schema.RequiredMode.REQUIRED)
    private String city;

    @NotBlank(message = "identityType is required")
    @Schema(example = "CNI", requiredMode = Schema.RequiredMode.REQUIRED)
    private String identityType;

    @NotBlank(message = "identityValue is required")
    @Schema(example = "abcd123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String identityValue;
}