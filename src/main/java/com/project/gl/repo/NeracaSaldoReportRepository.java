package com.project.gl.repo;

import java.time.LocalDate;
import java.util.List;
import com.project.gl.model.response.NeracaSaldoRow;

public interface NeracaSaldoReportRepository {

    List<NeracaSaldoRow> getNeracaSaldo(
            Long unitId,
            LocalDate startDate,
            LocalDate endDate
    );
}