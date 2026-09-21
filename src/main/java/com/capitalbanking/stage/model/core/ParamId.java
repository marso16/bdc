package com.capitalbanking.stage.model.core;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import java.io.Serializable;

@Getter
@Setter
public class ParamId implements Serializable {
    private static final long serialVersionUID = -1867523831792065260L;

    @Id
    private String tname;

    @Id
    private String model;

    @Id
    private String x1;

    @Id
    private String x2;
}
