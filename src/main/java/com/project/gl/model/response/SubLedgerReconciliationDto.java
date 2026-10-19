package com.project.gl.model.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SubLedgerReconciliationDto {

    private Integer glAccountId;

    private String nomorGl;

    private String namaGlAccount;

    private String saldoNormal;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal openingBalance = BigDecimal.ZERO;

    private BigDecimal totalDebit = BigDecimal.ZERO;

    private BigDecimal totalCredit = BigDecimal.ZERO;

    private BigDecimal saldoSubLedger = BigDecimal.ZERO;

    private BigDecimal saldoGl = BigDecimal.ZERO;

    private BigDecimal difference = BigDecimal.ZERO;

    private String status;
    
    private BigDecimal mutation;

    private List<SubLedgerDetailDto> details = new ArrayList<>();
}