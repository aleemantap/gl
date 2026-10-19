package com.project.gl.model.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface GlCashMovementProjection {

    Long getId();

    Long getJournalHeaderId();

    LocalDateTime getTransactionDate();

    String getJournalNo();

    String getTransactionNo();

    String getTransactionCode();

    String getBranchCode();

    String getDirection();

    BigDecimal getAmount();

    Long getTellerUserId();

    String getKasPointCode();

    String getCounterpartyType();

    String getCounterpartyRef();

    String getCounterpartyName();

    String getDescription();

    LocalDateTime getOccurredAt();
}