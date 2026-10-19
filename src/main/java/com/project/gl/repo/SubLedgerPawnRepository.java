package com.project.gl.repo;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface SubLedgerPawnRepository {

    BigDecimal getActiveLoanBalance(LocalDate date);

    BigDecimal getAdvanceBalance(LocalDate date);

    BigDecimal getAccruedInterest(LocalDate date);
}