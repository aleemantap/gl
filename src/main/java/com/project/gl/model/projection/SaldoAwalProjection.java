package com.project.gl.model.projection;
import java.math.BigDecimal;
public interface SaldoAwalProjection {
    BigDecimal getTotalDebit();
    BigDecimal getTotalCredit();
    String getNormalBalance();
}