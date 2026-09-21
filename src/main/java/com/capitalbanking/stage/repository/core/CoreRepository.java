package com.capitalbanking.stage.repository.core;

import com.capitalbanking.stage.model.core.Param;
import com.capitalbanking.stage.shared.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class CoreRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreRepository.class);

    private final ParamRepository paramRepository;
    private final EntityManager em;

    public CoreRepository(ParamRepository paramRepository, EntityManager em) {
        this.paramRepository = paramRepository;
        this.em = em;
    }

    // table state
    public String getBankCodbnq() {
        return getCodbnq();
    }

    private String getCodbnq() {
        String sql = "SELECT p.iso || LPAD(t.codbnq, 6, '0') FROM state t, pays p WHERE p.pays = t.pays";
        try {
            Object result = em.createNativeQuery(sql)
                    .getSingleResult();
            return result != null ? result.toString() : null;
        } catch (Exception e) {
            LOGGER.error("Failed to execute getStateField query: {}", e.getMessage());
            return null;
        }
    }

    public String getModev(String operation, String clientType) {
        String y1 = getFx5y8ValueWithX4(
                operation, clientType);
        LOGGER.info("getModev: operation={}, clientType={} → y1={}", operation, clientType, y1);
        return y1;
    }

    // table fx5y8
    public String getFx5y8Value(String tname, String model, String x1, String x2) {
        Param param = Param.builder()
                .tname(tname)
                .model(model)
                .x1(x1)
                .x2(x2)
                .build();
        List<Param> results = paramRepository.getParamBy(param);
        return (results != null && !results.isEmpty()) ? results.get(0).getY1() : null;
    }

    private String getFx5y8ValueWithX4(String x1, String x4) {
        Param param = Param.builder()
                .tname("RI_COMMONS")
                .model("MODEV")
                .x1(x1)
                .x2(Constants.MODEV_X2)
                .x4(x4)
                .build();
        List<Param> results = paramRepository.getParamBy(param);
        return (results != null && !results.isEmpty()) ? results.get(0).getY1() : null;
    }

    public String getDepotPoolAccount() {
        String sql = "SELECT y1 FROM fx5y8 WHERE tname = 'RI_COMMONS' AND model = 'POOLACC' AND x1 = 'DEPOT'";
        try {
            Object result = em.createNativeQuery(sql).getSingleResult();
            return result != null ? result.toString() : null;
        } catch (Exception e) {
            LOGGER.error("Failed to get depot pool account: {}", e.getMessage());
            return null;
        }
    }

    public String getRetraitPoolAccount() {
        String sql = "SELECT y1 FROM fx5y8 WHERE tname = 'RI_COMMONS' AND model = 'POOLACC' AND x1 = 'RETRAIT'";
        try {
            Object result = em.createNativeQuery(sql).getSingleResult();
            return result != null ? result.toString() : null;
        } catch (Exception e) {
            LOGGER.error("Failed to get retrait pool account: {}", e.getMessage());
            return null;
        }
    }
}