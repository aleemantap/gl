package com.project.gl.model.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface LaporanLabaRugiDetailProjection {

    LocalDateTime getTanggal();

    String getNomorJurnal();

    String getNomorTransaksi();

    String getNomorGl();

    String getNamaGlAccount();

    String getDk();

    BigDecimal getDebit();

    BigDecimal getKredit();

    BigDecimal getSaldo();
}