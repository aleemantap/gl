package com.project.gl.model.entity;


import java.math.BigDecimal;
import java.time.LocalDate;

import com.project.gl.model.enums.OjkRatioOperator;
import com.project.gl.model.enums.OjkRatioRegion;
import com.project.gl.model.enums.OjkRatioUnit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "ojk_ratio_config")
@Getter
@Setter
public class OjkRatioConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ratio_code", nullable = false, length = 50)
    private String ratioCode;

    @Column(name = "ratio_name", nullable = false, length = 150)
    private String ratioName;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "region_scope", nullable = false)
    private OjkRatioRegion regionScope;

    @Enumerated(EnumType.STRING)
    @Column(name = "comparison_operator", nullable = false)
    private OjkRatioOperator comparisonOperator;

    @Column(name = "threshold_value", precision = 20, scale = 4)
    private BigDecimal thresholdValue;

    @Column(name = "warning_margin_pct", precision = 8, scale = 2)
    private BigDecimal warningMarginPct;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OjkRatioUnit unit;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}