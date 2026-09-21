package com.capitalbanking.stage.shared;

public class Constants {

    public static final String STATUS_OK = "OK";
    public static final String STATUS_KO = "KO";
    public static final String STATUS_BAD_REQUEST = "Bad Request";
    public static final String STATUS_ACCEPTED = "ACCEPTED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_PENDING = "PENDING";
    public static final String CALL_SRC_VIRINT = "VIRINT";
    public static final String FLAG_DISPONIBLE = "DISPONIBLE";
    public static final String FLAG_TRUE = "TRUE";
    public static final String FLAG_FALSE = "FALSE";

    public static final String CODE_200 = "200";
    public static final String CODE_400 = "400";

    public static final String AUDIENCE_WEB = "web";

    public static final String OTP = "otp";
    public static final String GRANT_TYPE = "grant_type";
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String SCOPE = "scope";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    public static final String UTF_8_ENCODING = "UTF-8";
    public static final String SCOPE_READ = "read";
    public static final String _REFRESH = "_refresh";
    public static final String BEARER = "Bearer";

    public static final int CONNECT_TIMEOUT_MS = 10_000;
    public static final int READ_TIMEOUT_MS = 30_000;

    public static final String MODEV_X1_DEPOT = "DEPOT";
    public static final String MODEV_X1_RETRAIT = "RETRAIT";
    public static final String MODEV_X2 = "VIRINT";
    public static final String MODEV_X4_CLICLI = "CLI";
    public static final String MODEV_X4_CLINCLI = "NCLI";

    public static final String ERR_CODE_300 = "00300";
    public static final String ERR_CODE_301 = "00301";
    public static final String ERR_CODE_304 = "00304";
    public static final String ERR_CODE_399 = "00399";
    public static final String ERR_CODE_500 = "00500";
    public static final String ERR_CODE_1000 = "01000";
    public static final String ERR_CODE_200 = "00200";

    public static final String ERR_MSG_300 = "Les comptes de débit et de crédit doivent être différents";
    public static final String ERR_MSG_301 = "Le compte n'est pas valide. Il n'existe pas";
    public static final String ERR_MSG_304 = "Le numéro de compte fourni est vide ou invalide";
    public static final String ERR_MSG_399 = "Données erronées.";
    public static final String ERR_MSG_500 = "Erreur interne du système";
    public static final String ERR_MSG_1000 = "Transaction Id non valide";

    public static final String INTENT_DIRECT_CASH_IN = "direct_cash_in";
    public static final String INTENT_DIRECT_CASH_OUT = "direct_cash_out";
    public static final String STATE_ACCEPTED = "ACCEPTED";
    public static final String STATE_REJECTED = "REJECTED";
}