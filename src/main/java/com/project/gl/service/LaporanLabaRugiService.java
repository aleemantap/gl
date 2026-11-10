package com.project.gl.service;
import java.time.YearMonth;
import java.util.List;

import com.project.gl.model.response.LaporanLabaRugiDetailResponse;
import com.project.gl.model.response.LaporanLabaRugiResponse;

public interface LaporanLabaRugiService {

    List<LaporanLabaRugiResponse> getLaporan(
        YearMonth periode,
        Long cabangId
    );

    List<LaporanLabaRugiDetailResponse> getDetailJurnal(
        String kodePos,
        YearMonth periode,
        Long cabangId
    );

    
}