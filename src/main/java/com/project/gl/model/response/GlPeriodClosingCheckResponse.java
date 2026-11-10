package com.project.gl.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.project.gl.model.enums.GlPeriodClosingStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GlPeriodClosingCheckResponse {

    private Integer tahun;
    private Integer bulan;
    private Integer cabangId;
    private String kodeCabang;
    private String namaCabang;

    // Pemeriksaan jurnal
    private Long totalJournal;
    private BigDecimal totalDebit;
    private BigDecimal totalKredit;
    private boolean neracaSeimbang;

    // Pemeriksaan CKPN
    private boolean ckpnCreated;
    private String statusCkpn;
    private BigDecimal totalCadanganGadai;
    private BigDecimal saldoCadanganGl;

    // Antrean Gadai
    private Integer pendingGadaiCount;

    // Hasil akhir
    private boolean canSubmit;
    private List<String> errors;

    private String submittedBy;
    private LocalDateTime submittedAt;

    private String approvedBy;
    private LocalDateTime approvedAt;

    private String rejectionReason;

    private String pendingGadaiNote;


    private GlPeriodClosingStatus status;

  
    
}