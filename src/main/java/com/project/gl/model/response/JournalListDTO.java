package com.project.gl.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// import groovy.transform.builder.Builder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalListDTO {

    private Long id;

    private String journalNo;

    private String transactionNo;

    private String transactionCode;

    private String branchCode;

    private LocalDateTime transactionDate;

    private BigDecimal totalDebit;

    private BigDecimal totalCredit;

    private String status;

    private String reversalStatus;

    private String namaCabang;
}