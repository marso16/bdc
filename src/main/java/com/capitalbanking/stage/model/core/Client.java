package com.capitalbanking.stage.model.core;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Setter
@Getter
@Entity
@Table(name = "cli")
public class Client {
    @Id
    @Column(name = "client")
    private String client;
    private String nom;
    private Date datouv;
    private Date datfrm;
}
