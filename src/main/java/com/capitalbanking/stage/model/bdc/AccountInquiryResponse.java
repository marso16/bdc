package com.capitalbanking.stage.model.bdc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Réponse du service AccountInquiry.")
public class AccountInquiryResponse {

    @ApiModelProperty(value = "Description courte de l'erreur. Vide si succès.")
    private String error;

    @ApiModelProperty(value = "Description longue de l'erreur. Vide si succès.")
    private String error_description;

    @ApiModelProperty(value = "Statut de la transaction: ACCEPTED ou PENDING.", example = "ACCEPTED")
    private String state;

    @ApiModelProperty(value = "Référence de la transaction côté banque du client.", example = "134813000429")
    private String acquirertrxref;

    @ApiModelProperty(value = "Données du titulaire du compte.")
    private ReceiverCustomerData receivercustomerdata;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReceiverCustomerData {
        private String firstname;
        private String secondname;
        private String middlename;
        private String surname;
        private String name;
        private String idtype;
        private String idnumber;
        private String address;
        private String city;
        private String country;
        private String phone;
        private String email;
        private String gender;
        private String birthdate;
    }
}