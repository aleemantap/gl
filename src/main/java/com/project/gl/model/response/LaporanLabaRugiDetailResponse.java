package com.project.gl.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class LaporanLabaRugiDetailResponse {

    private LocalDateTime tanggal;
    private String nomorJurnal;
    private String nomorTransaksi;

    private String nomorGl;
    private String namaGlAccount;

    private String dk;
    private BigDecimal debit;
    private BigDecimal kredit;
    private BigDecimal saldo;
}