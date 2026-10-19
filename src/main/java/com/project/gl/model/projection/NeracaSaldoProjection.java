package com.project.gl.model.projection;

import java.math.BigDecimal;

public interface NeracaSaldoProjection {

     Long getGlAccountId();

    String getAccountCode();

    String getAccountName();

    String getNormalBalance();

    BigDecimal getDebitSebelum();

    BigDecimal getKreditSebelum();

    BigDecimal getTotalDebit();

    BigDecimal getTotalKredit();
}