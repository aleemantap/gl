
package com.project.gl.controller.laporan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.poi.ss.usermodel.*;
import java.nio.file.Path;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.gl.controller.BaseController;
import com.project.gl.helper.JournalCodeGenerator;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.NomorGlAccount;
import com.project.gl.model.response.BukuBesarPageResponse;
import com.project.gl.model.response.BukuBesarResponse;
import com.project.gl.model.response.JournalEntryResponse;
import com.project.gl.model.response.JournalListDTO;
import com.project.gl.model.response.KodeTransaksiResponse;
import com.project.gl.model.response.NomorGelAccountResponse;
import com.project.gl.model.response.PageResponse;
import com.project.gl.service.CabangService;
import com.project.gl.service.NomorGlAccountService;
import com.project.gl.service.JournalEntryService;
import com.project.gl.service.JournalIdService;
import com.project.gl.service.KodeTransaksiService;
import com.project.gl.service.BukuBesarService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import com.project.gl.model.response.BukuBesarPageResponse;

@Controller
@RequestMapping("/laporan/buku-besar") 
@Slf4j
public class BukuBesarController extends  BaseController{

    @Autowired
    private NomorGlAccountService nomorGlAccountService;

    @Autowired
    private CabangService cabangService;

    @Autowired
    private BukuBesarService bukuBesarService;

    private ObjectMapper objectMapper;


    @GetMapping
    public String index(Model model) {
        try {
            // Data cabang untuk filter
            List<Cabang> cabangList = cabangService.getAll();
            model.addAttribute("cabangList", cabangList);

            List<NomorGelAccountResponse> gl = nomorGlAccountService.getAll();
            model.addAttribute("glAccount", gl);            
            
            // Default date (bulan berjalan)
            LocalDate now = LocalDate.now();
            String tanggalDari = now.withDayOfMonth(1).toString();
            String tanggalSampai = now.toString();
            
            model.addAttribute("tanggalDari", tanggalDari);
            model.addAttribute("tanggalSampai", tanggalSampai);
            model.addAttribute("breadcrumbItems", new String[]{"LAPORAN", "BUKU BESAR"});
            
        } catch (Exception e) {
            log.error("Error loading Buku Besar page: {}", e.getMessage());
            model.addAttribute("error", "Gagal memuat halaman: " + e.getMessage());
        }
        setActiveMenu(model, "buku-besar", "laporan");
        return "laporan/buku-besar/index";
    }

    @GetMapping("/datatables")
    @ResponseBody
    public BukuBesarPageResponse getBukuBesar(
            @RequestParam(required = false) Long glAccountId,  // Made optional
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate tanggalDari,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate tanggalSampai,
            @RequestParam(required = false) String branchCode
    ) {

        LocalDateTime dari = tanggalDari.atStartOfDay();
        LocalDateTime sampai = tanggalSampai.plusDays(1).atStartOfDay();
        String ids = null;
        return bukuBesarService.getBukuBesar(
                ids,
                glAccountId,
                dari,
                sampai,
                branchCode
        );
    }


        
    @GetMapping("/export/excel")
    public void exportBukuBesarExcel(
            @RequestParam(required = false) String ids,
            @RequestParam(required = false) Long glAccountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggalDari,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggalSampai,
            @RequestParam(required = false) String branchCode,
            HttpServletResponse response) throws IOException {

        // 1. Handling Null & Parsing Tanggal
        LocalDateTime dari = (tanggalDari != null) ? tanggalDari.atStartOfDay() : null;
        LocalDateTime sampai = (tanggalSampai != null) ? tanggalSampai.plusDays(1).atStartOfDay() : null;
      
        // 2. Fetch Data dari Buku Besar Service
        BukuBesarPageResponse bukuBesarPage = bukuBesarService.getBukuBesar(
                ids,
                glAccountId,
                dari,
                sampai,
                branchCode
        );

        List<BukuBesarResponse> items = (bukuBesarPage != null) ? bukuBesarPage.getData() : null;

        // 3. Set Response Header
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=buku_besar.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Buku Besar");

        // Header Style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // Columns Definition
        String[] columns = {
                "Tanggal Transaksi",
                "No. Jurnal",
                "No. Transaksi",
                "Kode Cabang",
                "Nama Cabang",
                "No. Akun (GL)",
                "Nama Akun",
                "Keterangan",
                "Debit",
                "Kredit",
                "Saldo Berjalan"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        // 4. Tambahkan Baris Saldo Awal (Opsional tapi direkomendasikan untuk Buku Besar)
        // if (bukuBesarPage != null && bukuBesarPage.getSaldoAwal() != null) {
        //     Row saldoAwalRow = sheet.createRow(rowNum++);
        //     saldoAwalRow.createCell(0).setCellValue("-");
        //     saldoAwalRow.createCell(6).setCellValue("SALDO AWAL");
        //     saldoAwalRow.createCell(9).setCellValue(bukuBesarPage.getSaldoAwal().doubleValue());
        // }

        // 5. Populate Data Transaksi
        if (items != null) {
            for (BukuBesarResponse item : items) {
                Row row = sheet.createRow(rowNum++);

                // Tanggal
                if (item.getTransactionDate() != null) {
                    row.createCell(0).setCellValue(item.getTransactionDate().format(dateFormatter));
                } else {
                    row.createCell(0).setCellValue("");
                }

                row.createCell(1).setCellValue(item.getJournalNo() != null ? item.getJournalNo() : "");
                row.createCell(2).setCellValue(item.getTransactionNo() != null ? item.getTransactionNo() : "");
                row.createCell(3).setCellValue(item.getBranchCode() != null ? item.getBranchCode() : "");
                row.createCell(4).setCellValue(item.getBranchName() != null ? item.getBranchName() : "");

                row.createCell(5).setCellValue(item.getAccountCode() != null ? item.getAccountCode() : "");
                row.createCell(6).setCellValue(item.getAccountName() != null ? item.getAccountName() : "");
                row.createCell(7).setCellValue(item.getDescription() != null ? item.getDescription() : "");

                // Separasi Nilai Debit / Kredit berdasarkan posisi 'dk'
                double amount = (item.getAmount() != null) ? item.getAmount().doubleValue() : 0.0;
                if ("D".equalsIgnoreCase(item.getDk())) {
                    row.createCell(8).setCellValue(amount);
                    row.createCell(9).setCellValue(0.0);
                } else if ("K".equalsIgnoreCase(item.getDk())) {
                    row.createCell(8).setCellValue(0.0);
                    row.createCell(9).setCellValue(amount);
                } else {
                    row.createCell(8).setCellValue(0.0);
                    row.createCell(9).setCellValue(0.0);
                }

                // Saldo Berjalan
                row.createCell(10).setCellValue(item.getSaldoBerjalan() != null ? item.getSaldoBerjalan().doubleValue() : 0.0);
            }
        }

        // Auto-size kolom
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Output ke Response Stream
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
        }
    }

 

}