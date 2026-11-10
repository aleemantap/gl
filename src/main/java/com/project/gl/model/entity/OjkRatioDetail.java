package com.project.gl.model.entity;

import java.math.BigDecimal;

import com.project.gl.model.enums.OjkRatioDetailStatus;
import com.project.gl.model.enums.OjkRatioOperator;
import com.project.gl.model.enums.OjkRatioSubjectType;
import com.project.gl.model.enums.OjkRatioUnit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "ojk_ratio_detail",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_ojk_ratio_detail",
            columnNames = {
                "report_id",
                "ratio_code",
                "subject_type",
                "subject_ref"
            }
        )
    }
)
@Getter
@Setter
public class OjkRatioDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private OjkRatioReport report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "config_id")
    private OjkRatioConfig config;

    @Column(name = "ratio_code", nullable = false, length = 50)
    private String ratioCode;

    @Column(name = "ratio_name", nullable = false, length = 150)
    private String ratioName;

    @Enumerated(EnumType.STRING)
    @Column(name = "subject_type", nullable = false)
    private OjkRatioSubjectType subjectType = OjkRatioSubjectType.NONE;

    @Column(name = "subject_ref", nullable = false, length = 100)
    private String subjectRef = "";

    @Column(name = "subject_name", length = 200)
    private String subjectName;

    @Column(name = "numerator", precision = 20, scale = 2)
    private BigDecimal numerator = BigDecimal.ZERO;

    @Column(name = "denominator", precision = 20, scale = 2)
    private BigDecimal denominator = BigDecimal.ZERO;

    @Column(name = "result_value", precision = 20, scale = 4)
    private BigDecimal resultValue = BigDecimal.ZERO;

    @Column(name = "threshold_value", nullable = false, precision = 20, scale = 4)
    private BigDecimal thresholdValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "comparison_operator", nullable = false)
    private OjkRatioOperator comparisonOperator;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OjkRatioUnit unit = OjkRatioUnit.PERCENT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OjkRatioDetailStatus status;

    @Column(name = "calculation_note", length = 1000)
    private String calculationNote;

    @PrePersist
    protected void onCreate() {
        if (subjectRef == null) {
            subjectRef = "";
        }
        if (numerator == null) {
            numerator = BigDecimal.ZERO;
        }
        if (denominator == null) {
            denominator = BigDecimal.ZERO;
        }
        if (resultValue == null) {
            resultValue = BigDecimal.ZERO;
        }
    }
}