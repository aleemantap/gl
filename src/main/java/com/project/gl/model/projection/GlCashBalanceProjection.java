package com.project.gl.model.projection;

import java.math.BigDecimal;

public interface GlCashBalanceProjection {

    Long getCabangId();
    Long getGlAccountId();
    BigDecimal getSaldo();
}