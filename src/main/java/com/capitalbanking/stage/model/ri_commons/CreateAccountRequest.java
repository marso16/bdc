package com.capitalbanking.stage.model.ri_commons;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateAccountRequest {
    private String transId;
    private String bank;
    private String firstName;
    private String middleName;
    private String surname;
    private String lastName;
    private String gender;
    private String birthdate;
    private String gsm;
    private String email;
    private String address;
    private String nationality;
    private String country;
    private String region;
    private String city;
    private String identityType;
    private String identityValue;
}