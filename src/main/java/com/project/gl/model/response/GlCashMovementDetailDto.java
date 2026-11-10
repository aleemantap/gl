package com.project.gl.model.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class GlCashMovementDetailDto {

    private String transactionCode;
    private String transactionName;

    private BigDecimal kasMasuk = BigDecimal.ZERO;
    private BigDecimal kasKeluar = BigDecimal.ZERO;
}