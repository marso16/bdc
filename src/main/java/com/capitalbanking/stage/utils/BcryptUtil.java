package com.capitalbanking.stage.utils;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class BcryptUtil {

    static public String hashPassword(String plainTextPassword) {

        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());

    }

    public static void main(String[] args) {
        System.out.println("Allin@BankPROD === " + hashPassword("Ecash@BciGnProd25"));
    }
}
