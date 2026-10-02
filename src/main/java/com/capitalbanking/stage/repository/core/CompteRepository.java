package com.capitalbanking.stage.repository.core;

import com.capitalbanking.stage.model.core.Compte;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CompteRepository extends JpaRepository<Compte, String> {
    Optional<Compte> findCompteByCompte(String compte);

    @Query(value =
            "select nvl(m.nom, x.jurlib1)                    as firstname, " +
                    "       m.nom2                           as secondname, " +
                    "       m.nompere                        as middlename, " +
                    "       m.prenom2                        as surname, " +
                    "       m.nom                            as name, " +
                    "       m.typeid                         as idtype, " +
                    "       m.numid                          as idnumber, " +
                    "       nvl(m.adr1, x.adr1)              as address, " +
                    "       nvl(m.adr5, x.adr5)              as city, " +
                    "       nvl(m.payres, x.paysjur)         as country, " +
                    "       nvl(m.tel, x.tel)                as phone, " +
                    "       nvl(m.mail, x.mail)              as email, " +
                    "       m.sexe                           as gender, " +
                    "       to_char(m.datnais, 'YYYY-MM-DD') as birthdate " +
                    "  from titu u, idp m, cli c, cpt p , idm  x" +
                    " where u.idp = m.idp(+) " +
                    "   and x.idm = u.idm(+)" +
                    "   and u.client = c.client " +
                    "   and ((u.tituprinc = 'O' AND u.poum = 'P') OR (u.poum = 'M'))" +
                    "   and c.client = p.client " +
                    "   and p.compte = :accountNumber " +
                    "   and p.datfrm is null ",
            nativeQuery = true)
    List<Object[]> findCustomerDataByAccount(String accountNumber);
}

