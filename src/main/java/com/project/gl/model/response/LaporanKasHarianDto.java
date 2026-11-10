package com.project.gl.model.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class LaporanKasHarianDto {

    private Long cabangId;
    private String kodeCabang;
    private String namaCabang;

    private LocalDate tanggal;

    private BigDecimal saldoAwalKas = BigDecimal.ZERO;
    private BigDecimal saldoAwalKasKecil = BigDecimal.ZERO;

    private BigDecimal kasMasuk = BigDecimal.ZERO;
    private BigDecimal kasKeluar = BigDecimal.ZERO;

    private BigDecimal saldoAkhirKas = BigDecimal.ZERO;
    private BigDecimal saldoAkhirKasKecil = BigDecimal.ZERO;

    private BigDecimal totalSaldoAkhir = BigDecimal.ZERO;

    private List<GlCashMovementDetailDto> details;
}