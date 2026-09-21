package com.capitalbanking.stage.model.bdc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Requête du service PaymentInquiry (BCC → Participant).")
public class PaymentInquiryRequest {

    @Schema(description = "Numéro de compte source.", example = "acc000000001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String accountid;

    @Schema(description = "Toujours true.", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean asissuer;

    @Schema(description = "Nombre de transactions à retourner.", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pagesize;

    @Schema(description = "Index du premier élément (négatif = premier).", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer firstelement;

    @Schema(description = "Filtre par référence de transaction.", example = "295588436467")
    private String paymentreference;
}