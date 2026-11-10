package com.project.gl.model.response;

import com.project.gl.model.enums.OjkRatioRegion;
import com.project.gl.model.enums.OjkRatioStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OjkRatioReportListDto(
        Long id,
        String reportNo,
        LocalDate periode,
        OjkRatioRegion regionScope,
        BigDecimal totalEquity,
        BigDecimal paidUpCapital,
        OjkRatioStatus status
) {
}