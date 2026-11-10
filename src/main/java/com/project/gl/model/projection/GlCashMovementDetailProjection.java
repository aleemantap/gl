package com.project.gl.model.projection;

import java.math.BigDecimal;

public interface GlCashMovementDetailProjection {

    String getTransactionCode();
    String getTransactionName();
    String getDirection();
    BigDecimal getTotalAmount();
}