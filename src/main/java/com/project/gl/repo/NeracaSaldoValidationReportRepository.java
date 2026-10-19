package com.project.gl.repo;
import java.util.List;

import com.project.gl.model.response.NeracaSaldoValidationRow;

public interface NeracaSaldoValidationReportRepository {

    List<NeracaSaldoValidationRow> findRecentValidations(int limit);

}