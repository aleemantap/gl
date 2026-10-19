package com.project.gl.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntryResponse {
    private Long id;
    private String journalNo;
    private LocalDateTime  transactionDate;
    private String transactionNo;
    private String kode;
    private String kodeCabang;
    private Integer glAccountId;
    private String nomorGl;
    private String namaGlAccount;
    private String description;
    private String dk;
    private BigDecimal amount;
    private String status;
    private String namaCabang;

   
}