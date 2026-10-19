package com.project.gl.controller.laporan;


import java.time.LocalDate;

import org.springframework.web.bind.annotation.GetMapping;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.response.NomorGelAccountResponse;
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

import com.project.gl.controller.BaseController;
import com.project.gl.helper.JournalCodeGenerator;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.NomorGlAccount;
import com.project.gl.model.projection.NeracaSaldoProjection;
import com.project.gl.model.response.BukuBesarPageResponse;
import com.project.gl.model.response.BukuBesarResponse;
import com.project.gl.model.response.JournalEntryResponse;
import com.project.gl.model.response.KodeTransaksiResponse;
import com.project.gl.model.response.LogKustodiKasDTO;
import com.project.gl.model.response.NeracaSaldoDTO;
import com.project.gl.repo.NeracaSaldoRepository;
import com.project.gl.model.response.PageResponse;
import com.project.gl.service.CabangService;
import com.project.gl.service.NomorGlAccountService;
import com.project.gl.service.LogKustodiKasService;
import com.project.gl.service.JournalIdService;
import com.project.gl.service.KodeTransaksiService;
import com.project.gl.service.NeracaSaldoService;
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

@Controller
@RequestMapping("/laporan/log-kustodi-kas") 
public class LogKustodiController extends BaseController {

    @Autowired
    private NomorGlAccountService nomorGlAccountService;

    @Autowired
    private CabangService cabangService;

    @Autowired
    private NeracaSaldoRepository neracaSaldoRepository;

    @Autowired
    private LogKustodiKasService logKustodiKasService;

    

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
            setActiveMenu(model, "log-kustodi-kas", "laporan");   
            model.addAttribute("tanggalDari", tanggalDari);
            model.addAttribute("tanggalSampai", tanggalSampai);
            model.addAttribute("breadcrumbItems", new String[]{"LAPORAN", "LOG KUSTODI KAS"});
            
        } catch (Exception e) {
            //log.error("Error loading Buku Besar page: {}", e.getMessage());
            model.addAttribute("error", "Gagal memuat halaman: " + e.getMessage());
        }
        
        return "laporan/log-studi-kas/index";
    }

    @GetMapping("/datatables")
    @ResponseBody
    public Map<String, Object> getDataTables(

            @RequestParam int draw,
            @RequestParam int start,
            @RequestParam int length,

            @RequestParam(required = false)
            String tanggalDari,

            @RequestParam(required = false)
            String tanggalSampai,

            @RequestParam(required = false)
            String branchCode,

            @RequestParam(required = false)
            String direction
    ) {

        int page = start / length;

        PageResponse<LogKustodiKasDTO> response =
                logKustodiKasService.getData(
                        page,
                        length,
                        tanggalDari != null && !tanggalDari.isBlank()
                                ? LocalDate.parse(tanggalDari)
                                : null,
                        tanggalSampai != null && !tanggalSampai.isBlank()
                                ? LocalDate.parse(tanggalSampai)
                                : null,
                        branchCode,
                        direction
                );

        Map<String, Object> result = new HashMap<>();
        result.put("draw", draw);
        result.put("recordsTotal",response.getTotalElements());
        result.put("recordsFiltered",response.getTotalElements());
        result.put("data",response.getContent());
        return result;
    }

 

    @GetMapping("/export-excel")
    public void  exportExcel(
                @RequestParam(required = false) String ids,
                @RequestParam(required = false) String tanggalDari,
                @RequestParam(required = false) String tanggalSampai,
                @RequestParam(required = false) String branchCode,
                @RequestParam(required = false) String direction,
                HttpServletResponse response
                )throws IOException  {
              
                List<LogKustodiKasDTO> items = logKustodiKasService.getDataExport(ids,
                        tanggalDari != null && !tanggalDari.isBlank()
                                ? LocalDate.parse(tanggalDari)
                                : null,
                        tanggalSampai != null && !tanggalSampai.isBlank()
                                ? LocalDate.parse(tanggalSampai)
                                : null,
                        branchCode,
                        direction

                );
               // 3. Set Response Header
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment; filename=log_transaksi.xlsx");

                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("LOG TRANSAKSI");

                // Header Style
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);

                // Columns Definition
                String[] columns = {
                        "WAKTU TRX GADAI",
                        "WAKTU INSERT",
                        "ARAH",
                        "NOMINAL",
                        "TALLER",
                        "KETERANGAN",
                        "Kas Point Code",
                        "Counterparty Name",
                        "Counterparty Type",
                        "Counterparty Ref",
                        "Journal No",
                        "Branch Code"
                      
                       
                };


                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < columns.length; i++) {
                        Cell cell = headerRow.createCell(i);
                        cell.setCellValue(columns[i]);
                        cell.setCellStyle(headerStyle);
                }
                int rowNum = 1;
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

                 if (items != null) {
                        for (LogKustodiKasDTO item : items) {
                                Row row = sheet.createRow(rowNum++);

                                // Tanggal
                                if (item.getOccurredAt() != null) {
                                    row.createCell(0).setCellValue(item.getOccurredAt().format(dateFormatter));
                                } else {
                                     row.createCell(0).setCellValue("");
                                }


                                if (item.getTransactionDate() != null) {
                                    row.createCell(1).setCellValue(item.getTransactionDate().format(dateFormatter));
                                } else {
                                     row.createCell(1).setCellValue("");
                                }
                                double amount = (item.getAmount() != null) ? item.getAmount().doubleValue() : 0.0;
               
                                row.createCell(2).setCellValue(item.getDirection() != null ? item.getDirection() : "");
                                row.createCell(3).setCellValue(amount);
                                row.createCell(4).setCellValue(item.getTellerUserId() != null ? item.getTellerUserId() : 0);
                                row.createCell(5).setCellValue(item.getDescription() != null ? item.getDescription() : "");
                                row.createCell(6).setCellValue(item.getKasPointCode() != null ? item.getKasPointCode() : "");
                                row.createCell(7).setCellValue(item.getCounterpartyName() != null ? item.getCounterpartyName() : "");
                                row.createCell(8).setCellValue(item.getCounterpartyType() != null ? item.getCounterpartyType() : "");
                                row.createCell(9).setCellValue(item.getCounterpartyRef() != null ? item.getCounterpartyRef() : "");                               
                                row.createCell(10).setCellValue(item.getJournalNo() != null ? item.getJournalNo() : "");                              
                                row.createCell(11).setCellValue(item.getBranchCode() != null ? item.getBranchCode() : "");
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
