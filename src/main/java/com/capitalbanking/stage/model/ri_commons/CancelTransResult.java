package com.capitalbanking.stage.model.ri_commons;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CancelTransResult {
    String transId;
    String status;
    BigDecimal soldeDisp;
    String errorCode;
    String errorMsg;
    String devise;
}