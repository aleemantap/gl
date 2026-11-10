package com.project.gl.model.projection;

import java.math.BigDecimal;

public interface GlCashMovementTotalProjection {
    String getDirection();
    BigDecimal getTotalAmount();
}