package com.project.gl.model.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface JournalListProjection {

    Long getId();

    String getJournalNo();

    String getTransactionNo();

    String getTransactionCode();

    String getBranchCode();

    LocalDateTime getTransactionDate();

    BigDecimal getTotalDebit();

    BigDecimal getTotalCredit();

    String getStatus();

    String getReversalStatus();

    String getNamaCabang();
}