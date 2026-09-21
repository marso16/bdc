package com.capitalbanking.stage.repository.core;

import com.capitalbanking.stage.model.core.Compte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CompteRepository extends JpaRepository<Compte, String> {
    Optional<Compte> findCompteByCompte(String compte);

    @Query(value =
            "select m.nom                            as firstname, " +
                    "       m.nom2                           as secondname, " +
                    "       m.nompere                        as middlename, " +
                    "       m.prenom2                        as surname, " +
                    "       m.nom                            as name, " +
                    "       m.typeid                         as idtype, " +
                    "       m.numid                          as idnumber, " +
                    "       m.adr1                           as address, " +
                    "       m.adr5                           as city, " +
                    "       m.payres                         as country, " +
                    "       m.tel                            as phone, " +
                    "       m.mail                           as email, " +
                    "       m.sexe                           as gender, " +
                    "       to_char(m.datnais, 'YYYY-MM-DD') as birthdate " +
                    "  from titu u, idp m, cli c, cpt p " +
                    " where u.idp = m.idp " +
                    "   and u.client = c.client " +
                    "   and u.tituprinc = 'O' " +
                    "   and c.client = p.client " +
                    "   and p.compte = :accountNumber " +
                    "   and p.datfrm is null " +
                    "   and rownum = 1",
            nativeQuery = true)
    List<Object[]> findCustomerDataByAccount(String accountNumber);
}

