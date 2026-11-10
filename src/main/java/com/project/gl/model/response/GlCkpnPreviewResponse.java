package com.project.gl.model.response;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import lombok.*;

@Data
@Builder
public class GlCkpnPreviewResponse {

    private String periode;
    private LocalDate tanggalPosisi;

    private Integer cabangId;
    private String kodeCabang;
    private String namaCabang;

    private String referensiLaporan;

    private BigDecimal totalCadanganGadai;
    private BigDecimal saldoCadanganGl;

    // Selisih bertanda: positif berarti cadangan Gadai lebih besar
    private BigDecimal selisih;

    // Nominal jurnal selalu positif
    private BigDecimal nominalJurnal;

    private String arahJurnal;
    private boolean jurnalDiperlukan;

    private String status;
}