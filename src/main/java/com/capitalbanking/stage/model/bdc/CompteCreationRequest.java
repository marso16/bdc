package com.capitalbanking.stage.model.bdc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@ApiModel(description = "Requête de création de compte (BCC -> Participant).")
public class CompteCreationRequest {

    @NotBlank(message = "L'identifiant de la demande (requestID) est obligatoire")
    @ApiModelProperty(example = "FNB1235D4635", required = true)
    private String requestID;

    @NotBlank(message = "La banque (bank) est obligatoire")
    @ApiModelProperty(example = "bank001", required = true)
    private String bank;

    @NotBlank(message = "Le prénom (firstName) est obligatoire")
    @ApiModelProperty(example = "John", required = true)
    private String firstName;

    @ApiModelProperty(example = "William")
    private String middleName;

    @ApiModelProperty
    private String surname;

    @NotBlank(message = "Le nom de famille (lastName) est obligatoire")
    @ApiModelProperty(example = "Doe", required = true)
    private String lastName;

    @NotBlank(message = "Le genre (gender) est obligatoire")
    @Pattern(regexp = "^[MF]$", message = "Le genre (gender) doit être M ou F")
    @ApiModelProperty(example = "M", required = true)
    private String gender;

    @NotBlank(message = "La date de naissance (birthdate) est obligatoire")
    @ApiModelProperty(example = "794837754346", required = true)
    private String birthdate;

    @NotBlank(message = "Le numéro de téléphone (gsm) est obligatoire")
    @Pattern(regexp = "^[0-9]+$", message = "Le numéro de téléphone (gsm) doit être numérique")
    @ApiModelProperty(example = "123456789", required = true)
    private String gsm;

    @NotBlank(message = "L'adresse e-mail (email) est obligatoire")
    @Email(message = "L'adresse e-mail (email) doit être valide")
    @ApiModelProperty(example = "johndoe@gmail.com", required = true)
    private String email;

    @NotBlank(message = "L'adresse (address) est obligatoire")
    @ApiModelProperty(example = "23 Rue de la Santé", required = true)
    private String address;

    @NotBlank(message = "La nationalité (nationality) est obligatoire")
    @ApiModelProperty(example = "COM", required = true)
    private String nationality;

    @NotBlank(message = "Le pays (country) est obligatoire")
    @ApiModelProperty(example = "COM", required = true)
    private String country;

    @ApiModelProperty(example = "REGION MORONI")
    private String region;

    @NotBlank(message = "La ville (city) est obligatoire")
    @ApiModelProperty(example = "MORONI", required = true)
    private String city;

    @NotBlank(message = "Le type de pièce d'identité (identityType) est obligatoire")
    @ApiModelProperty(example = "CNI", required = true)
    private String identityType;

    @NotBlank(message = "Le numéro de pièce d'identité (identityValue) est obligatoire")
    @ApiModelProperty(example = "abcd123", required = true)
    private String identityValue;
}