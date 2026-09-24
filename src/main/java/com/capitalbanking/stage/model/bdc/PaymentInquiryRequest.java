package com.capitalbanking.stage.model.bdc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "Requête du service PaymentInquiry (BCC -> Participant).")
public class PaymentInquiryRequest {

    @ApiModelProperty(value = "Numéro de compte source.", example = "acc000000001", required = true)
    private String accountid;

    @ApiModelProperty(value = "Toujours true.", example = "true", required = true)
    private Boolean asissuer;

    @ApiModelProperty(value = "Nombre de transactions à retourner.", example = "10", required = true)
    private Integer pagesize;

    @ApiModelProperty(value = "Index du premier élément (négatif = premier).", example = "1", required = true)
    private Integer firstelement;

    @ApiModelProperty(value = "Filtre par référence de transaction.", example = "295588436467")
    private String paymentreference;
}