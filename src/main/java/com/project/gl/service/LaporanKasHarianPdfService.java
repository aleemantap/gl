package com.project.gl.service;

import java.io.ByteArrayInputStream;

import com.project.gl.model.response.LaporanKasHarianDto;

public interface LaporanKasHarianPdfService {

    ByteArrayInputStream generatePdf(LaporanKasHarianDto data);

}