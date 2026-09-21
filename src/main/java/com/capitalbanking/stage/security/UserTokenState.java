package com.capitalbanking.stage.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserTokenState {
    private String token;
    private int expire;
    private String type;
}