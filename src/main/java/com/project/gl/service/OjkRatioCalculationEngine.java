package com.project.gl.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.project.gl.model.entity.OjkRatioConfig;
import com.project.gl.model.enums.OjkRatioDetailStatus;
import com.project.gl.model.enums.OjkRatioOperator;
import com.project.gl.model.enums.OjkRatioUnit;
import com.project.gl.model.response.OjkRatioCalculationResult;

@Service
public class OjkRatioCalculationEngine {

    private static final int SCALE = 4;

    public OjkRatioCalculationResult calculateRatio(
            String ratioCode,
            String ratioName,
            BigDecimal numerator,
            BigDecimal denominator,
            OjkRatioConfig config
    ) {
        numerator = zeroIfNull(numerator);
        denominator = zeroIfNull(denominator);

        BigDecimal result = BigDecimal.ZERO;

        if (config.getUnit() == OjkRatioUnit.PERCENT) {
            if (denominator.compareTo(BigDecimal.ZERO) > 0) {
                result = numerator
                        .multiply(BigDecimal.valueOf(100))
                        .divide(denominator, SCALE, RoundingMode.HALF_UP);
            }
        } else {
            result = numerator;
        }

        OjkRatioDetailStatus status = determineStatus(
                result,
                config.getThresholdValue(),
                config.getComparisonOperator(),
                config.getWarningMarginPct()
        );

        return OjkRatioCalculationResult.builder()
                .ratioCode(ratioCode)
                .ratioName(ratioName)
                .numerator(numerator)
                .denominator(denominator)
                .resultValue(result)
                .thresholdValue(config.getThresholdValue())
                .comparisonOperator(config.getComparisonOperator())
                .unit(config.getUnit())
                .status(status)
                .build();
    }

    private OjkRatioDetailStatus determineStatus(
            BigDecimal result,
            BigDecimal threshold,
            OjkRatioOperator operator,
            BigDecimal warningMargin
    ) {
        if (threshold == null || operator == null) {
            throw new IllegalArgumentException(
                    "Konfigurasi batas rasio belum lengkap"
            );
        }

        BigDecimal margin = warningMargin != null
                ? warningMargin
                : BigDecimal.ZERO;

        if (operator == OjkRatioOperator.MIN) {

            if (result.compareTo(threshold) < 0) {
                return OjkRatioDetailStatus.BREACH;
            }

            BigDecimal warningLimit = threshold.multiply(
                    BigDecimal.ONE.add(
                            margin.divide(
                                    BigDecimal.valueOf(100),
                                    6,
                                    RoundingMode.HALF_UP
                            )
                    )
            );

            if (result.compareTo(warningLimit) < 0) {
                return OjkRatioDetailStatus.WARNING;
            }

        } else {

            if (result.compareTo(threshold) > 0) {
                return OjkRatioDetailStatus.BREACH;
            }

            BigDecimal warningLimit = threshold.multiply(
                    BigDecimal.ONE.subtract(
                            margin.divide(
                                    BigDecimal.valueOf(100),
                                    6,
                                    RoundingMode.HALF_UP
                            )
                    )
            );

            if (result.compareTo(warningLimit) > 0) {
                return OjkRatioDetailStatus.WARNING;
            }
        }

        return OjkRatioDetailStatus.SAFE;
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}