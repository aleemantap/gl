package com.project.gl.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.project.gl.model.entity.OjkRatioDetail;
import com.project.gl.model.entity.OjkRatioReport;
import com.project.gl.model.projection.OjkProfitLossProjection;
import com.project.gl.model.request.OjkRatioCalculationRequest;

public interface OjkRatioService {

    OjkRatioReport calculate(OjkRatioCalculationRequest request);

    OjkRatioReport getReport(LocalDate periode);

    List<OjkRatioReport> getAllReports();
    OjkProfitLossProjection getProfitLoss(LocalDate periode);
    BigDecimal zeroIfNull(BigDecimal value);

    OjkRatioReport getReportById(Long id);

    List<OjkRatioDetail> getReportDetails(Long reportId);
}