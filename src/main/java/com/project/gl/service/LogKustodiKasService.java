package com.project.gl.service;

import java.time.LocalDate;
import java.util.List;

import com.project.gl.model.response.LogKustodiKasDTO;
import com.project.gl.model.response.PageResponse;

public interface LogKustodiKasService {

    PageResponse<LogKustodiKasDTO> getData(
            int page,
            int size,
            LocalDate tanggalDari,
            LocalDate tanggalSampai,
            String branchCode,
            String direction
    );

    List<LogKustodiKasDTO> getDataExport(
            String ids, 
            LocalDate tanggalDari,
            LocalDate tanggalSampai,
            String branchCode,
            String direction
        );
    
}