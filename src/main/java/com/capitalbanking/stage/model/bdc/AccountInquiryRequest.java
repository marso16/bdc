package com.capitalbanking.stage.model.bdc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "Requête du service AccountInquiry (BCC → Participant).")
public class AccountInquiryRequest {

    @Schema(description = "Opération demandée. Valeur fixe: account_inquiry", example = "account_inquiry", requiredMode = Schema.RequiredMode.REQUIRED)
    private String intent;

    @Schema(description = "Liste des comptes destination [{iden: account_number, type: ACCOUNT}]", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<DstAccount> dstaccounts;

    @Schema(description = "Référence unique de transaction émise par le Switch.", example = "1572602202520", requiredMode = Schema.RequiredMode.REQUIRED)
    private String issuertrxref;

    @Schema(description = "Identifiant du participant source (banque de l'agent).", example = "0001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String frommember;

    @Schema(description = "Identifiant du participant destination (banque du client).", example = "0002", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tomember;

    @Schema(description = "Code OTP / voucher de la transaction.", example = "12345678", requiredMode = Schema.RequiredMode.REQUIRED)
    private String vouchercode;

    @Getter
    @Setter
    public static class DstAccount {
        @Schema(description = "Numéro de compte destination.", example = "acc000000001")
        private String iden;

        @Schema(description = "Type de compte. Valeur fixe: ACCOUNT", example = "ACCOUNT")
        private String type;
    }
}