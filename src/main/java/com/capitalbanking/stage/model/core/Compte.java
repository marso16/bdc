package com.capitalbanking.stage.model.core;

import lombok.*;

import javax.persistence.*;
import java.sql.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cpt")
public class Compte {
    @Id
    @Column(name = "compte")
    private String compte;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "client")
    private Client client;
    private String devise;

    private String nom;
    private String typ;
    private String ncg;
    private String agence;
    private Double posdisp;
    private Double posdev;
    private Date datouv;
    private Date datfrm;
    private String coddci;
    private String oplist;

    @Transient
    private String label;
}