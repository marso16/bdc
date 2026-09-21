package com.capitalbanking.stage.model.core;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "fx5y8")
@IdClass(ParamId.class)
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Param {

    @Id
    private String tname;

    @Id
    private String model;

    @Id
    private String x1;

    @Id
    private String x2;

    private String x3;

    private String x4;

    private String y1;
    private String y2;
    private String y3;
    private String y4;
}