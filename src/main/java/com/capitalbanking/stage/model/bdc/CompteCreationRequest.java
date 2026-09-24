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

    @NotBlank(message = "requestID is required")
    @ApiModelProperty(example = "FNB1235D4635", required = true)
    private String requestID;

    @NotBlank(message = "bank is required")
    @ApiModelProperty(example = "bank001", required = true)
    private String bank;

    @NotBlank(message = "firstName is required")
    @ApiModelProperty(example = "John", required = true)
    private String firstName;

    @ApiModelProperty(example = "William")
    private String middleName;

    @ApiModelProperty
    private String surname;

    @NotBlank(message = "lastName is required")
    @ApiModelProperty(example = "Doe", required = true)
    private String lastName;

    @NotBlank(message = "gender is required")
    @Pattern(regexp = "^[MF]$", message = "gender must be M or F")
    @ApiModelProperty(example = "M", required = true)
    private String gender;

    @NotBlank(message = "birthdate is required")
    @ApiModelProperty(example = "794837754346", required = true)
    private String birthdate;

    @NotBlank(message = "gsm is required")
    @Pattern(regexp = "^[0-9]+$", message = "gsm must be numeric")
    @ApiModelProperty(example = "123456789", required = true)
    private String gsm;

    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    @ApiModelProperty(example = "johndoe@gmail.com", required = true)
    private String email;

    @NotBlank(message = "address is required")
    @ApiModelProperty(example = "23 Rue de la Santé", required = true)
    private String address;

    @NotBlank(message = "nationality is required")
    @ApiModelProperty(example = "COM", required = true)
    private String nationality;

    @NotBlank(message = "country is required")
    @ApiModelProperty(example = "COM", required = true)
    private String country;

    @ApiModelProperty(example = "REGION MORONI")
    private String region;

    @NotBlank(message = "city is required")
    @ApiModelProperty(example = "MORONI", required = true)
    private String city;

    @NotBlank(message = "identityType is required")
    @ApiModelProperty(example = "CNI", required = true)
    private String identityType;

    @NotBlank(message = "identityValue is required")
    @ApiModelProperty(example = "abcd123", required = true)
    private String identityValue;
}