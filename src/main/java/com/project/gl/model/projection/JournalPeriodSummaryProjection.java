package com.project.gl.model.projection;

import java.math.BigDecimal;

public interface JournalPeriodSummaryProjection {

    BigDecimal getTotalDebit();
    BigDecimal getTotalKredit();
    Long getTotalJournal();
}