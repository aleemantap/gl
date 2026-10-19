package com.project.gl.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JournalDetailResponse {

    private Long id;
    private String journalNo;
    private String transactionNo;
    private String transactionCode;
    private String branchCode;
    private LocalDateTime transactionDate;
    private String status;

    private BigDecimal totalDebit;
    private BigDecimal totalCredit;

    private List<JournalDetailLineResponse> details;
}