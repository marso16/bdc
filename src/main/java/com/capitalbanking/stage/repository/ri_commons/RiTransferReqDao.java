package com.capitalbanking.stage.repository.ri_commons;

import com.capitalbanking.stage.model.ri_commons.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Reader;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Types;

@Repository
public class RiTransferReqDao {

    private final JdbcTemplate jdbcTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(RiTransferReqDao.class);

    public RiTransferReqDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public String getTransactionStatus(String transId) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("PK_RI_COMMONS")
                .withFunctionName("getTransactionStatus")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlOutParameter("RETURN_VALUE", Types.CLOB),
                        new SqlParameter("p_trans_id", Types.VARCHAR)
                );

        MapSqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_trans_id", transId);

        return clobToString(call.executeFunction(Clob.class, in));
    }

    @Transactional
    public SaveInternalRequestResponse saveInternalRequest(SaveInternalRequestRequest req) {
        final String sql = "{ call pk_ri_commons.saveInternalRequest(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";

        return jdbcTemplate.execute((ConnectionCallback<SaveInternalRequestResponse>) con -> {
            try (CallableStatement cs = con.prepareCall(sql)) {
                int i = 1;

                cs.setString(i, req.getTransId());
                cs.registerOutParameter(i++, Types.VARCHAR);           // 1  p_transId IN OUT

                setNullableString(cs, i++, req.getRefrel());           // 2  p_i_payername
                setNullableDate(cs, i++, req.getPaymentDate());        // 3  p_i_paymentdate
                setNullableString(cs, i++, req.getMotif1());           // 4  p_i_paymentreference
                setNullableString(cs, i++, req.getPayerAccount());     // 5  p_i_payeraccount
                setNullableString(cs, i++, req.getBenefName());        // 6  p_i_benefname
                setNullableString(cs, i++, req.getBenefAccount());     // 7  p_i_benefaccount
                setNullableString(cs, i++, req.getBenefBicCode());     // 8  p_i_benefbiccode
                setNullableDouble(cs, i++, req.getAmount());           // 9  p_i_amount
                setNullableString(cs, i++, req.getBenefCurrency());    // 10 p_i_benefcurrency
                setNullableString(cs, i++, req.getModev());            // 11 p_i_modev
                setNullableString(cs, i++, req.getCallSrc());          // 12 p_i_callsrc

                cs.registerOutParameter(i++, Types.VARCHAR);           // 13 p_status
                cs.registerOutParameter(i++, Types.VARCHAR);           // 14 p_o_referencetrans
                cs.registerOutParameter(i++, Types.VARCHAR);           // 15 p_o_banktransid
                cs.registerOutParameter(i++, Types.NUMERIC);           // 16 p_o_soldedispdebacc
                cs.registerOutParameter(i++, Types.NUMERIC);           // 17 p_o_soldecptadebacc
                cs.registerOutParameter(i++, Types.VARCHAR);           // 18 p_errorCode
                cs.registerOutParameter(i++, Types.VARCHAR);           // 19 p_errorMsg
                cs.registerOutParameter(i++, Types.VARCHAR);           // 20 p_devise

                LOGGER.info("saveInternalRequest IN  >> transId={}, refrel={}, paymentDate={}, motif1={}, payerAccount={}, " +
                                "benefName={}, benefAccount={}, benefBicCode={}, amount={}, benefCurrency={}, modev={}, callSrc={}",
                        req.getTransId(), req.getRefrel(), req.getPaymentDate(), req.getMotif1(),
                        req.getPayerAccount(), req.getBenefName(), req.getBenefAccount(),
                        req.getBenefBicCode(), req.getAmount(), req.getBenefCurrency(),
                        req.getModev(), req.getCallSrc());

                cs.execute();

                SaveInternalRequestResponse resp = SaveInternalRequestResponse.builder()
                        .transId(cs.getString(1))
                        .status(cs.getString(13))
                        .referenceTrans(cs.getString(14))
                        .bankReference(cs.getString(15))
                        .soldeDispDebAcc(cs.getBigDecimal(16))
                        .soldeCptaDebAcc(cs.getBigDecimal(17))
                        .errorCode(cs.getString(18))
                        .errorMsg(cs.getString(19))
                        .devise(cs.getString(20))
                        .build();

                LOGGER.info("saveInternalRequest OUT >> transId={}, status={}, referenceTrans={}, " +
                                "bankReference={}, soldeDispDebAcc={}, soldeCptaDebAcc={}, errorCode={}, errorMsg={}, devise={}",
                        resp.getTransId(), resp.getStatus(), resp.getReferenceTrans(),
                        resp.getBankReference(), resp.getSoldeDispDebAcc(), resp.getSoldeCptaDebAcc(),
                        resp.getErrorCode(), resp.getErrorMsg(), resp.getDevise());

                return resp;
            }
        });
    }

    public RequestSoldeResponse requestSolde(
            String transId,
            String accountNumber,
            String credAcc,
            String withFee,
            BigDecimal feeAmount,
            String associationCode) {

        final String sql = "{ ? = call pk_ri_commons.requestSolde(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";

        return jdbcTemplate.execute((ConnectionCallback<RequestSoldeResponse>) con -> {
            try (CallableStatement cs = con.prepareCall(sql)) {
                int i = 1;

                cs.registerOutParameter(i++, Types.NUMERIC);        // 1  RETURN value

                setNullableString(cs, i++, transId);                // 2  p_transId
                setNullableString(cs, i++, accountNumber);          // 3  p_accountNumber
                setNullableString(cs, i++, credAcc);                // 4  p_credAcc
                cs.setString(i++, "N");                          // 5  p_withFee
                setNullableBigDecimal(cs, i++, feeAmount);          // 6  p_feeAmount
                setNullableString(cs, i++, associationCode);        // 7  p_associationCode

                cs.registerOutParameter(i++, Types.VARCHAR);        // 8  p_soldeDisp
                cs.registerOutParameter(i++, Types.VARCHAR);        // 9  p_reference_core
                cs.registerOutParameter(i++, Types.VARCHAR);        // 10 p_errorCode
                cs.registerOutParameter(i++, Types.VARCHAR);        // 11 p_errorMsg
                cs.registerOutParameter(i++, Types.VARCHAR);        // 12 p_status
                cs.registerOutParameter(i++, Types.VARCHAR);        // 13 p_devise

                cs.execute();

                return RequestSoldeResponse.builder()
                        .soldeDisp(cs.getString(8))
                        .bankReference(cs.getString(9))
                        .errorCode(cs.getString(10))
                        .errorMsg(cs.getString(11))
                        .status(cs.getString(12))
                        .devise(cs.getString(13))
                        .build();
            }
        });
    }

    @Transactional
    public CancelTransResult cancelTrans(CancelTransRequest req) {
        final String sql = "{ call pk_ri_commons.cancelTrans(?, ?, ?, ?, ?, ?, ?, ?) }";

        return jdbcTemplate.execute((ConnectionCallback<CancelTransResult>) con -> {
            try (CallableStatement cs = con.prepareCall(sql)) {
                int i = 1;

                cs.setString(i, nullIfBlank(req.getTransId()));
                cs.registerOutParameter(i++, Types.VARCHAR);                    // 1  p_i_transid  IN OUT

                setNullableString(cs, i++, nullIfBlank(req.getAnnulTransId())); // 2  p_i_annulTransId

                setNullableDate(cs, i++, req.getDate());                        // 3  p_date

                cs.registerOutParameter(i++, Types.VARCHAR);                    // 4  p_status
                cs.registerOutParameter(i++, Types.NUMERIC);                    // 5  p_soldeDisp
                cs.registerOutParameter(i++, Types.VARCHAR);                    // 6  p_errorCode
                cs.registerOutParameter(i++, Types.VARCHAR);                    // 7  p_errorMsg
                cs.registerOutParameter(i++, Types.VARCHAR);                    // 8  p_devise

                cs.execute();

                return CancelTransResult.builder()
                        .transId(cs.getString(1))
                        .status(cs.getString(4))
                        .soldeDisp(cs.getBigDecimal(5))
                        .errorCode(cs.getString(6))
                        .errorMsg(cs.getString(7))
                        .devise(cs.getString(8))
                        .build();
            }
        });
    }

    public CreateAccountResponse createAccount(CreateAccountRequest req) {
        final String sql = "{ call pk_ri_commons.createAccount(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";

        return jdbcTemplate.execute((ConnectionCallback<CreateAccountResponse>) con -> {
            try (CallableStatement cs = con.prepareCall(sql)) {
                int i = 1;

                cs.setString(i++, req.getTransId());         // 1
                cs.setString(i++, req.getBank());            // 2
                cs.setString(i++, req.getFirstName());       // 3
                cs.setString(i++, req.getMiddleName());      // 4
                cs.setString(i++, req.getSurname());         // 5
                cs.setString(i++, req.getLastName());        // 6
                cs.setString(i++, req.getGender());          // 7
                cs.setString(i++, req.getBirthdate());       // 8
                cs.setString(i++, req.getGsm());             // 9
                cs.setString(i++, req.getEmail());           // 10
                cs.setString(i++, req.getAddress());         // 11
                cs.setString(i++, req.getNationality());     // 12
                cs.setString(i++, req.getCountry());         // 13
                cs.setString(i++, req.getRegion());          // 14
                cs.setString(i++, req.getCity());            // 15
                cs.setString(i++, req.getIdentityType());    // 16
                cs.setString(i++, req.getIdentityValue());   // 17

                cs.registerOutParameter(i++, Types.VARCHAR); // 18 p_o_account
                cs.registerOutParameter(i++, Types.VARCHAR); // 19 p_o_client
                cs.registerOutParameter(i++, Types.VARCHAR); // 20 p_status
                cs.registerOutParameter(i++, Types.VARCHAR); // 21 p_errorCode
                cs.registerOutParameter(i++, Types.VARCHAR); // 22 p_errorMsg

                cs.execute();

                return CreateAccountResponse.builder()
                        .accountNumber(cs.getString(18))
                        .clientId(cs.getString(19))
                        .status(cs.getString(20))
                        .errorCode(cs.getString(21))
                        .errorMsg(cs.getString(22))
                        .build();
            } catch (Exception e) {
                throw new RuntimeException("createAccount procedure failed: " + e.getMessage(), e);
            }
        });
    }

    private static void setNullableString(CallableStatement cs, int index, String value)
            throws SQLException {
        if (value == null) cs.setNull(index, Types.VARCHAR);
        else cs.setString(index, value);
    }

    private static void setNullableBigDecimal(CallableStatement cs, int index, BigDecimal value)
            throws SQLException {
        if (value == null) cs.setNull(index, Types.NUMERIC);
        else cs.setBigDecimal(index, value);
    }

    private static void setNullableDouble(CallableStatement cs, int index, Double value)
            throws SQLException {
        if (value == null) cs.setNull(index, Types.NUMERIC);
        else cs.setDouble(index, value);
    }

    private static void setNullableDate(CallableStatement cs, int index, java.time.LocalDate value)
            throws SQLException {
        if (value == null) cs.setNull(index, Types.DATE);
        else cs.setDate(index, java.sql.Date.valueOf(value));
    }

    private static String clobToString(Clob clob) {
        if (clob == null) return null;
        try (Reader reader = clob.getCharacterStream()) {
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[4096];
            int n;
            while ((n = reader.read(buf)) > 0) sb.append(buf, 0, n);
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read CLOB from getTransactionStatus", e);
        }
    }

    private static String nullIfBlank(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s;
    }
}