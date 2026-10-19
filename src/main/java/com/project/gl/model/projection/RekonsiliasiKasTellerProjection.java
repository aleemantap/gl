package com.project.gl.model.projection;

import java.math.BigDecimal;
import java.time.LocalDate;
public interface RekonsiliasiKasTellerProjection {

    Long getId();

    Long getTellerUserId();

    String getKasPointCode();

    LocalDate getReconciliationDate();

    String getShiftCode();

    BigDecimal getOpeningCash();

    BigDecimal getCashAddition();

    BigDecimal getTotalCashIn();

    BigDecimal getTotalCashOut();

    BigDecimal getExpectedCash();

    BigDecimal getActualCash();

    BigDecimal getDifference();

    String getStatus();
}