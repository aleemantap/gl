package com.project.gl.model.response;

import java.math.BigDecimal;

import com.project.gl.model.enums.OjkRatioDetailStatus;
import com.project.gl.model.enums.OjkRatioOperator;
import com.project.gl.model.enums.OjkRatioSubjectType;
import com.project.gl.model.enums.OjkRatioUnit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OjkRatioCalculationResult {

    private String ratioCode;
    private String ratioName;

    private OjkRatioSubjectType subjectType;
    private String subjectRef;
    private String subjectName;

    private BigDecimal numerator;
    private BigDecimal denominator;

    private BigDecimal resultValue;
    private BigDecimal thresholdValue;

    private OjkRatioOperator comparisonOperator;
    private OjkRatioUnit unit;

    private OjkRatioDetailStatus status;

    private String calculationNote;
}