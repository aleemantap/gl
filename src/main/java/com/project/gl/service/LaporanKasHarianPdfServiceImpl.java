package com.project.gl.service;

import com.project.gl.model.response.LaporanKasHarianDto;
import com.project.gl.service.LaporanKasHarianPdfGenerator;
import com.project.gl.service.LaporanKasHarianPdfService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
@RequiredArgsConstructor
public class LaporanKasHarianPdfServiceImpl implements LaporanKasHarianPdfService {

    private final LaporanKasHarianPdfGenerator pdfGenerator;

    @Override
    public ByteArrayInputStream generatePdf(LaporanKasHarianDto data) {

        if (data == null) {
            throw new IllegalArgumentException(
                    "Data Laporan Kas Harian tidak boleh kosong"
            );
        }

        return pdfGenerator.generatePdf(data);
    }
}