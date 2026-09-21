package com.capitalbanking.stage.repository.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
public class FlagRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlagRepository.class);

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public boolean isCutOffTimeByFlag(String flag) {
        try {
            String sql = "SELECT DECODE(VALIDE, 'V', 'NOCUTOFF', 'CUTOFF') FROM flag WHERE FLAG = :flag";
            Object state = em.createNativeQuery(sql)
                    .setParameter("flag", flag)
                    .getSingleResult();
            return "CUTOFF".equals(state);
        } catch (Exception e) {
            LOGGER.error("Error in isCutOffTimeByFlag for flag={}", flag, e);
            return false;
        }
    }
}