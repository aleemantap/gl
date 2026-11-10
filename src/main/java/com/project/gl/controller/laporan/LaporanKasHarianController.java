package com.project.gl.controller.laporan;

import com.project.gl.controller.BaseController;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.response.LaporanKasHarianDto;
import com.project.gl.service.CabangService;
import com.project.gl.service.LaporanKasHarianPdfGenerator;
import com.project.gl.service.LaporanKasHarianPdfService;
import com.project.gl.service.LaporanKasHarianService;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/laporan/kas-harian")
@RequiredArgsConstructor
public class LaporanKasHarianController extends BaseController{

    private final LaporanKasHarianService laporanKasHarianService;
    private final CabangService cabangService;
    private final LaporanKasHarianPdfService laporanKasHarianPdfService;
    private final LaporanKasHarianPdfGenerator pdfGenerator;

    @GetMapping
    public String index(Model model) {

        model.addAttribute(
                "breadcrumbItems",
                new String[]{"LAPORAN", "KAS HARIAN"}
        );

        setActiveMenu(model, "laporan-kas-harian", "laporan");

        List<Cabang> cabangList = cabangService.getAllWithFilter(
                0, 100, "namaCabang", "ASC", null, null
        ).getContent();

        model.addAttribute("cabangList", cabangList);
        model.addAttribute("tanggal", LocalDate.now());

        return "laporan/kas-harian/index";
    }

    @GetMapping("/data")
    @ResponseBody
    public LaporanKasHarianDto getData(
            @RequestParam Long cabangId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate tanggal
    ) {

        LaporanKasHarianDto dto = laporanKasHarianService.getLaporan(cabangId, tanggal);

        Cabang cabang = cabangService.getById(cabangId.intValue());

        dto.setKodeCabang(cabang.getKodeCabang());
        dto.setNamaCabang(cabang.getNamaCabang());

        return dto;
    }


    // Endpoint sementara untuk testing service
    @GetMapping("/test")
    @ResponseBody
    public LaporanKasHarianDto testLaporanKasHarian() {

        return laporanKasHarianService.getLaporan(
                2L,
                LocalDate.of(2026, 10, 2)
        );
    }

    @GetMapping("/export-pdf")
    public ResponseEntity<InputStreamResource> exportPdf(
            @RequestParam Long cabangId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate tanggal
    ) {

        LaporanKasHarianDto data =
                laporanKasHarianService.getLaporan(cabangId, tanggal);

        Cabang cabang = cabangService.getById(cabangId.intValue());

        data.setKodeCabang(cabang.getKodeCabang());
        data.setNamaCabang(cabang.getNamaCabang());

        ByteArrayInputStream pdfStream =
                laporanKasHarianPdfService.generatePdf(data);

        HttpHeaders headers = new HttpHeaders();

        headers.add(
                "Content-Disposition",
                "attachment; filename=Laporan_Kas_Harian_"
                        + cabang.getKodeCabang()
                        + "_"
                        + tanggal
                        + ".pdf"
        );

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfStream));
    }
}