package com.project.gl.controller.laporan;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.gl.controller.BaseController;
import com.project.gl.helper.JournalExcelExporter;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.projection.JournalListProjection;
import com.project.gl.model.response.JournalDetailResponse;
import com.project.gl.model.response.JournalListDTO;
import com.project.gl.model.response.KodeTransaksiResponse;
import com.project.gl.model.response.PageResponse;
import com.project.gl.service.CabangService;
import com.project.gl.service.JournalReportService;
import com.project.gl.service.KodeTransaksiService;
import com.project.gl.service.NomorGlAccountService;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;

// @RestController
@Controller
@RequestMapping("/laporan/daftar-jurnal")
public class JournalReportController extends BaseController{

    @Autowired
    private JournalReportService journalReportService;  

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss",
                new Locale("id", "ID")
        );

    @Autowired
    private CabangService cabangService;

    @Autowired
    private KodeTransaksiService kodeTransaksiService;

    @Autowired
    private  JournalExcelExporter journalExcelExporter;

    @GetMapping
    public String index(Model model) {       
        List<Cabang> cabangList = cabangService.getAll(); 
        model.addAttribute("cabangList", cabangList);
        List<KodeTransaksiResponse> kodeTransaksiList = kodeTransaksiService.getAll();
        model.addAttribute("kodeTransaksiList", kodeTransaksiList);         
        model.addAttribute("breadcrumbItems", new String[]{"LAPORAN", "DAFTAR JURNAL" });
        setActiveMenu(model, "daftar-jurnal", "laporan");
        return "laporan/daftarjurnal/index";
    }


    @GetMapping("/datatables")
    @ResponseBody
    public Map<String, Object> getDataTables(

            @RequestParam int draw,
            @RequestParam int start,
            @RequestParam int length,
            @RequestParam int orderColumn,
            @RequestParam String orderDir,
            @RequestParam(required = false)  String fromDate,           
            @RequestParam(required = false)  String toDate,
            @RequestParam(required = false)  String branchCode,
            @RequestParam(required = false)  String transactionCode,
            @RequestParam(required = false)  String journalNo,
            @RequestParam(required = false)  String transactionNo,
            @RequestParam(required = false)  String status,
            @RequestParam(required = false)  String statusReversal
            
    ) {

        int page = start / length;
        int size = length;
        LocalDate from = null;
        LocalDate to = null;
        if (fromDate != null && !fromDate.isBlank()) {
            from = LocalDate.parse(fromDate);
        }
        if (toDate != null && !toDate.isBlank()) {
            to = LocalDate.parse(toDate);
        }

      

        String sortBy = getSortColumn(orderColumn);
        PageResponse<JournalListDTO> response = journalReportService.findJournalList(

                        page,
                        size,
                        sortBy,
                        orderDir,
                        from,
                        to,
                        branchCode,
                        transactionCode,
                        journalNo,
                        transactionNo,
                        status,
                        statusReversal
                );
        Map<String, Object> result = new HashMap<>();
        result.put("draw", draw);
        result.put("recordsTotal", response.getTotalElements());
        result.put("recordsFiltered",response.getTotalElements());
        result.put("data",response.getContent());
        return result;
    }

    private String getSortColumn(int orderColumn) {

        return switch (orderColumn) {

            case 0 -> "journalNo";
            case 1 -> "transactionNo";
            case 2 -> "transactionCode";
            case 3 -> "branchCode";
            case 4 -> "transactionDate";
            case 5 -> "totalDebit";
            case 6 -> "totalCredit";
            case 7 -> "status";

            default -> "transactionDate";
        };
    }

    @GetMapping("/detail/{*journalNo}")
    public String detail(Model model, @PathVariable String journalNo) {    
        if (journalNo.startsWith("/")) {
            journalNo = journalNo.substring(1);
        }   
        JournalDetailResponse detail = journalReportService.getJournalDetail(journalNo);  
        model.addAttribute("detail", detail);      
        model.addAttribute("breadcrumbItems", new String[]{"LAPORAN", "DETAIL DAFTAR JURNAL",journalNo });
        return "laporan/daftarjurnal/detail";
    }

   

   @GetMapping("/export/excel")
    public void exportExcel(
            @RequestParam(required = false) String ids,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) String branchCode,
            @RequestParam(required = false) String transactionCode,
            @RequestParam(required = false) String journalNo,
            @RequestParam(required = false) String transactionNo,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String statusReversal,
            HttpServletResponse response) throws IOException {

        // 1. Ambil Data dari Service
        List<JournalListDTO> journals = journalReportService.findJournalListForExport(
                ids, fromDate, toDate, branchCode, transactionCode, journalNo, transactionNo, status, statusReversal
        );

        // 2. Generate File Excel
        journalExcelExporter.export(journals, response);
    }
}