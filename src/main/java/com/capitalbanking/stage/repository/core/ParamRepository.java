package com.capitalbanking.stage.repository.core;

import com.capitalbanking.stage.model.core.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class ParamRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public List<Param> getParamBy(Param param) {
        StringBuilder jpql = new StringBuilder("SELECT p FROM Param p WHERE 1=1");

        if (isSet(param.getTname())) jpql.append(" AND p.tname = :tname");
        if (isSet(param.getModel())) jpql.append(" AND p.model = :model");
        if (isSet(param.getX1())) jpql.append(" AND p.x1 = :x1");
        if (isSet(param.getX2())) jpql.append(" AND p.x2 = :x2");
        if (isSet(param.getX3())) jpql.append(" AND p.x3 = :x3");
        if (isSet(param.getX4())) jpql.append(" AND p.x4 = :x4");

        TypedQuery<Param> query = em.createQuery(jpql.toString(), Param.class);

        if (isSet(param.getTname())) query.setParameter("tname", param.getTname());
        if (isSet(param.getModel())) query.setParameter("model", param.getModel());
        if (isSet(param.getX1())) query.setParameter("x1", param.getX1());
        if (isSet(param.getX2())) query.setParameter("x2", param.getX2());
        if (isSet(param.getX3())) query.setParameter("x3", param.getX3());
        if (isSet(param.getX4())) query.setParameter("x4", param.getX4());

        return query.getResultList();
    }

    private boolean isSet(String value) {
        return value != null && !value.trim().isEmpty();
    }
}