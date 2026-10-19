package com.project.gl.controller.laporan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.poi.ss.usermodel.*;
import java.nio.file.Path;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.gl.helper.JournalCodeGenerator;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.response.JournalEntryResponse;
import com.project.gl.model.response.KodeTransaksiResponse;
import com.project.gl.model.response.NomorGelAccountResponse;
import com.project.gl.model.response.PageResponse;
import com.project.gl.service.CabangService;
import com.project.gl.service.JournalEntryService;
import com.project.gl.service.JournalIdService;
import com.project.gl.service.KodeTransaksiService;
import com.project.gl.service.NomorGlAccountService;
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
@RequestMapping("/laporan-x/daftar-jurnal-sx") 
@Slf4j
public class JournalController {

    @Autowired
    private NomorGlAccountService service;  

    @Autowired
    private CabangService cabangService;

    @Autowired
    private KodeTransaksiService kodeTransaksiService;

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private JournalCodeGenerator codeGenerator;


    private static final DateTimeFormatter DATE_TIME_FORMATTER =
        DateTimeFormatter.ofPattern(
                "dd MMMM yyyy HH:mm:ss",
                new Locale("id", "ID")
        );

    
    @GetMapping
    public String dashboard(Model model) {       
        List<Cabang> cabangList = cabangService.getAll(); 
        model.addAttribute("cabangList", cabangList);
        List<KodeTransaksiResponse> kodeTransaksiList = kodeTransaksiService.getAll();
        model.addAttribute("kodeTransaksiList", kodeTransaksiList);         
        model.addAttribute("breadcrumbItems", new String[]{"LAPORAN", "DAFTAR JURNAL" });
        return "laporan/daftarJurnal/index";
    }
    @GetMapping("/jr")
    public String jr(Model model) {       
        List<Cabang> cabangList = cabangService.getAll(); 
        model.addAttribute("cabangList", cabangList);
        List<KodeTransaksiResponse> kodeTransaksiList = kodeTransaksiService.getAll();
        model.addAttribute("kodeTransaksiList", kodeTransaksiList);         
        model.addAttribute("breadcrumbItems", new String[]{"LAPORAN", "DAFTAR JURNAL" });
        return "laporan/daftarJurnal/jr/index";
    }
 

    @GetMapping("/datatables")
    @ResponseBody
    public Map<String, Object> getDataTables(
            @RequestParam int draw,
            @RequestParam int start,
            @RequestParam int length,
            @RequestParam(required = false, defaultValue = "0") int orderColumn,
            @RequestParam(required = false, defaultValue = "asc") String orderDir,
            @RequestParam(required = false) String periode,
            @RequestParam(required = false) Integer cabangId,
            @RequestParam(required = false) String kodeTc) {

        // Calculate page (DataTables menggunakan start untuk offset)
        int page = start / length;
        String sortBy = getSortColumn(orderColumn);

        LocalDateTime periodeStart = null;
        LocalDateTime periodeEnd = null;

        if (periode != null && !periode.isBlank()) {

            LocalDate tanggal = LocalDate.parse(periode);

            periodeStart = tanggal.atStartOfDay();

            periodeEnd = tanggal.plusDays(1).atStartOfDay();
        }

        // Call service
        PageResponse<JournalEntryResponse> response = journalEntryService
                .getJournalEntriesWithFilters(
                        page,
                        length,
                        sortBy,
                        orderDir,
                        periodeStart,
                        periodeEnd,
                        cabangId,
                        kodeTc
                      
                );

        // Prepare DataTables response
        Map<String, Object> result = new HashMap<>();
        result.put("draw", draw);
        result.put("recordsTotal", response.getTotalElements());
        result.put("recordsFiltered", response.getTotalElements());
        // result.put("pageTotal", 11);        
        result.put("data", response.getContent());

        log.info("DataTables response: totalElements={}, pageSize={}, contentSize={}", 
                response.getTotalElements(), response.getPageSize(), 
                response.getContent() != null ? response.getContent().size() : 0);

        return result;
    }

    private String getSortColumn(int orderColumn) {
        // Sesuaikan dengan urutan kolom di DataTables
        switch (orderColumn) {
            case 0: return "journalNo";
            case 1: return "transactionDate";
            case 2: return "transactionCode";
            case 3: return "branchCode";
            case 4: return "nomorGl";
            case 5: return "namaGlAccount";
            case 6: return "amount";
            case 7: return "dk";
            case 8: return "status";
            default: return "journalNo";
        }
    }


    @GetMapping("/export/count")
    public ResponseEntity<?> countExport(
            @ModelAttribute 
            @RequestParam(required = false) String periode,
            @RequestParam(required = false) Integer cabangId,
            @RequestParam(required = false) String kodeTc
    ) {

        int page = 0;
        String sortBy = getSortColumn(1);
        LocalDateTime periodeStart = null;
        LocalDateTime periodeEnd = null;
        if (periode != null && !periode.isBlank()) {
            LocalDate tanggal = LocalDate.parse(periode);
            periodeStart = tanggal.atStartOfDay();
            periodeEnd = tanggal.plusDays(1).atStartOfDay();
        }
          int length = 999990;
          String orderDir = "asc";
          PageResponse<JournalEntryResponse> response = journalEntryService
                .getJournalEntriesWithFilters(
                        page,
                        length,
                        sortBy,
                        orderDir,
                        periodeStart,
                        periodeEnd,
                        cabangId,
                        kodeTc
                      
                );

        Long total =  response.getTotalElements();
        return ResponseEntity.ok(Map.of("total", total));
    }

    @GetMapping("/export/excel")
    public void exportExcel(
        @ModelAttribute 
        @RequestParam(required = false) String periode,
        @RequestParam(required = false) Integer cabangId,
        @RequestParam(required = false) String kodeTc,
        HttpServletResponse response)
        throws IOException {
          
    String fileName = "Excel-report-jurnal";
    response.setContentType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    response.setHeader("Content-Disposition","attachment; filename=" + fileName + ".xlsx");

        int page = 0;
        String sortBy = getSortColumn(1);
        LocalDateTime periodeStart = null;
        LocalDateTime periodeEnd = null;
        if (periode != null && !periode.isBlank()) {
            LocalDate tanggal = LocalDate.parse(periode);
            periodeStart = tanggal.atStartOfDay();
            periodeEnd = tanggal.plusDays(1).atStartOfDay();
        }
          int length = 999990;
          String orderDir = "asc";
          PageResponse<JournalEntryResponse> resp = journalEntryService
                .getJournalEntriesWithFilters(
                        page,
                        length,
                        sortBy,
                        orderDir,
                        periodeStart,
                        periodeEnd,
                        cabangId,
                        kodeTc                      
                );

            
        List<JournalEntryResponse> datas = resp.getContent();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Daftar Jurnal");
        // HEADER STYLE
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        // HEADER
        Row headerRow = sheet.createRow(0);

        String[] columns = {
                "JOURNAL NO",
                "TANGGAL/WAKTU",
                "CABANG/TC",
                "GL ACCOUNT",
                "KETERANGAN",
                "D/K",
                "NOMINAL",
                "STATUS"
        };

        for (int i = 0; i < columns.length; i++) {

            Cell cell = headerRow.createCell(i);

            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        // DATA
        int rowNum = 1;
        // DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss", new Locale("id", "ID"));
        for (JournalEntryResponse d : datas) {

            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(d.getJournalNo());
            // row.createCell(1).setCellValue(d.getTransactionDate().toString()); 
            if (d.getTransactionDate() != null) {
                row.createCell(1).setCellValue(
                        d.getTransactionDate().format(DATE_TIME_FORMATTER)
                );
            } else {
                row.createCell(1).setCellValue("");
            }
            row.createCell(2).setCellValue(d.getNamaCabang()+" / "+ d.getKode()); //
            row.createCell(3).setCellValue(d.getNamaGlAccount());
            row.createCell(4).setCellValue(d.getDescription());
            row.createCell(5).setCellValue(d.getDk());
            // row.createCell(6).setCellValue(d.getAmount());            
            if (d.getAmount() != null) {
                row.createCell(6).setCellValue(
                    d.getAmount().doubleValue()
                );
            } else {
                row.createCell(6).setCellValue(0);
            }
            row.createCell(7).setCellValue(d.getStatus());
        }

        // AUTO SIZE
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }


    @GetMapping("/generate-random-id")
    public ResponseEntity<Map<String, Object>> generateJournalCode() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Hanya generate kode, tidak insert ke database
            String randomCode = codeGenerator.generateUniqueJournalCode();
            
            response.put("success", true);
            response.put("randomId", randomCode);
            response.put("message", "ID berhasil digenerate");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("randomId", null);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}
