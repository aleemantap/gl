package com.project.gl.service;

import com.project.gl.model.response.LaporanKasHarianDto;
import java.time.LocalDate;

public interface LaporanKasHarianService {

    LaporanKasHarianDto getLaporan(
        Long cabangId,
        LocalDate tanggal
    );
}