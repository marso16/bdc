package com.capitalbanking.stage.repository.ri_commons;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RiCommonsApiCallsDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(RiCommonsApiCallsDao.class);

    private final JdbcTemplate jdbcTemplate;

    public RiCommonsApiCallsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean reserveTraceIfAbsent(final String transId,
                                        final String typeOperation,
                                        final String requestJson,
                                        final String clientName,
                                        final String clientIp,
                                        final String serverName) {
        final String sql =
                "INSERT INTO ri_commons_api_calls " +
                        " (ID, TRANS_ID, TYPE_OPERATION, REQUEST_JSON, CLIENT_NAME, CLIENT_IP, SERVER_NAME, EXECUTION_TIME_MS) " +
                        "VALUES (ri_commons_api_calls_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, NULL)";

        try {
            jdbcTemplate.update(sql, ps -> {
                int i = 1;
                ps.setString(i++, transId);
                ps.setString(i++, typeOperation);
                ps.setString(i++, requestJson);
                ps.setString(i++, clientName);
                ps.setString(i++, clientIp);
                ps.setString(i++, serverName);
            });
            return true;
        } catch (DataIntegrityViolationException ex) {
            if (isUniqueConstraint(ex)) {
                return false;
            }
            throw ex;
        }
    }

    private static final ObjectMapper TRACE_MAPPER = new ObjectMapper();

    public void updateAfterCompletion(final String transId, final String responseJson, final Long executionTimeMs,
                                      final Integer httpStatusOrNull) {

        final StringBuilder sql = new StringBuilder("UPDATE ri_commons_api_calls SET ");
        final List<Object> args = new ArrayList<>();
        final List<Integer> types = new ArrayList<>();

        sql.append("response_json = ?, execution_time_ms = ?");
        args.add(responseJson);
        types.add(Types.VARCHAR);

        args.add(executionTimeMs);
        types.add(Types.BIGINT);

        try {
            if (responseJson != null && !responseJson.trim().isEmpty()) {
                JsonNode root = TRACE_MAPPER.readTree(responseJson);

                String errorCode = getText(root, "error");
                String errorMsg = getText(root, "error_description");
                String bankReference = getText(root, "acquirertrxref");

                if (errorCode != null) {
                    sql.append(", error_code = ?");
                    args.add(errorCode);
                    types.add(Types.VARCHAR);
                }
                if (errorMsg != null) {
                    sql.append(", error_msg = ?");
                    args.add(errorMsg);
                    types.add(Types.VARCHAR);
                }
                if (bankReference != null) {
                    sql.append(", reference_core = ?");
                    args.add(bankReference);
                    types.add(Types.VARCHAR);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("updateAfterCompletion: failed to parse responseJson, proceeding with base fields. json={}", responseJson, e);
        }

        if (httpStatusOrNull != null) {
            sql.append(", status = ?");
            args.add(httpStatusOrNull == 200 ? "OK" : "KO");
            types.add(Types.VARCHAR);
        }

        sql.append(" WHERE trans_id = ?");
        args.add(transId);
        types.add(Types.VARCHAR);

        int[] typesArray = types.stream().mapToInt(Integer::intValue).toArray();
        jdbcTemplate.update(sql.toString(), args.toArray(), typesArray);
    }

    private static String getText(JsonNode node, String field) {
        if (node == null) {
            return null;
        }
        JsonNode n = node.get(field);
        if (n == null || n.isNull()) {
            return null;
        }
        String s = n.asText(null);
        return (s == null || s.isEmpty()) ? null : s;
    }

    private boolean isUniqueConstraint(Throwable t) {
        Throwable cause = t;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        if (cause instanceof SQLException) {
            SQLException sql = (SQLException) cause;
            String state = sql.getSQLState();
            int code = sql.getErrorCode();
            return "23505".equals(state) || code == 1 || code == 1062 || code == 2627 || code == 2601;
        }
        String msg = cause.getMessage();
        return msg != null &&
                (msg.contains("ORA-00001") ||
                        msg.contains("duplicate key value") ||
                        msg.contains("Duplicate entry") ||
                        msg.contains("UNIQUE KEY constraint"));
    }
}
