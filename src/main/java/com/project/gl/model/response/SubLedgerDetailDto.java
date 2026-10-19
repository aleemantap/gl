package com.project.gl.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SubLedgerDetailDto {

    private LocalDateTime transactionDate;

    private String journalNo;

    private String transactionNo;

    private BigDecimal debit;

    private BigDecimal credit;

    private BigDecimal mutation;

    private BigDecimal balance;

    private String description;
}