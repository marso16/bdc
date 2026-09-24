package com.capitalbanking.stage.model.bdc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel(description = "Requête du service AccountInquiry (BCC -> Participant).")
public class AccountInquiryRequest {

    @ApiModelProperty(value = "Opération demandée. Valeur fixe: account_inquiry",
            example = "account_inquiry", required = true)
    private String intent;

    @ApiModelProperty(value = "Liste des comptes destination [{iden: account_number, type: ACCOUNT}]", required = true)
    private List<DstAccount> dstaccounts;

    @ApiModelProperty(value = "Référence unique de transaction émise par le Switch.",
            example = "1572602202520", required = true)
    private String issuertrxref;

    @ApiModelProperty(value = "Identifiant du participant source (banque de l'agent).",
            example = "0001", required = true)
    private String frommember;

    @ApiModelProperty(value = "Identifiant du participant destination (banque du client).",
            example = "0002", required = true)
    private String tomember;

    @ApiModelProperty(value = "Code OTP / voucher de la transaction.",
            example = "12345678", required = true)
    private String vouchercode;

    @Getter
    @Setter
    public static class DstAccount {
        @ApiModelProperty(value = "Numéro de compte destination.",
                example = "acc000000001")
        private String iden;

        @ApiModelProperty(value = "Type de compte. Valeur fixe: ACCOUNT",
                example = "ACCOUNT")
        private String type;
    }
}