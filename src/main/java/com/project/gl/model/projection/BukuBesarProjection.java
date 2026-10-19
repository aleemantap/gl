package com.project.gl.model.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
public interface BukuBesarProjection {

    Long getDetailId();

    Long getJournalHeaderId();

    LocalDateTime getTransactionDate();

    String getJournalNo();

    String getTransactionNo();

    String getTransactionCode();

    String getBranchCode();

    String getBranchName();

    Long getGlAccountId();

    String getAccountCode();

    String getAccountName();

    String getNormalBalance();

    String getDk();

    BigDecimal getAmount();

    String getDescription();
}