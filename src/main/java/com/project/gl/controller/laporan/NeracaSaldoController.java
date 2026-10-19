package com.project.gl.controller.laporan;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.gl.config.CustomUserPrincipal;
import com.project.gl.controller.BaseController;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.NeracaSaldoSubmission;
import com.project.gl.model.projection.NeracaSaldoProjection;
import com.project.gl.model.response.BukuBesarResponse;
import com.project.gl.model.response.NeracaSaldoDTO;
import com.project.gl.model.response.NomorGelAccountResponse;
import com.project.gl.model.response.PageResponse;
import com.project.gl.repo.NeracaSaldoRepository;
import com.project.gl.service.CabangService;
import com.project.gl.service.NeracaSaldoPdfGenerator;
import com.project.gl.service.NeracaSaldoService;
import com.project.gl.service.NeracaSaldoService2;
import com.project.gl.service.NomorGlAccountService;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping("/laporan/neraca-saldo")
@Slf4j
public class NeracaSaldoController extends BaseController {

    @Autowired
    private NomorGlAccountService nomorGlAccountService;

    @Autowired
    private CabangService cabangService;

    @Autowired
    private NeracaSaldoRepository neracaSaldoRepository;

    @Autowired
    private NeracaSaldoService neracaSaldoService;

    @Autowired
    private NeracaSaldoService2 neracaSaldoService2;

    @Autowired
    private NeracaSaldoPdfGenerator pdfGenerator;

    // =========================================
    // HALAMAN INDEX
    // =========================================

    @GetMapping
    public String index(Model model) {
        try {
            List<Cabang> cabangList = cabangService.getAll();
            model.addAttribute("cabangList", cabangList);

            List<NomorGelAccountResponse> gl = nomorGlAccountService.getAll();
            model.addAttribute("glAccount", gl);

            LocalDate now = LocalDate.now();
            String tanggalDari = now.withDayOfMonth(1).toString();
            String tanggalSampai = now.toString();

            model.addAttribute("tanggalDari", tanggalDari);
            model.addAttribute("tanggalSampai", tanggalSampai);
            model.addAttribute("breadcrumbItems", new String[]{"LAPORAN", "NERACA SALDO"});
            setActiveMenu(model, "neraca-saldo", "laporan");    
        } catch (Exception e) {
            log.error("Error loading Neraca Saldo page: {}", e.getMessage());
            model.addAttribute("error", "Gagal memuat halaman: " + e.getMessage());
        }

        return "laporan/neraca-saldo/index";
    }

    // =========================================
    // DATATABLES
    // =========================================

    @GetMapping("/datatables")
    @ResponseBody
    public Map<String, Object> getDataTables(
            @RequestParam int draw,
            @RequestParam int start,
            @RequestParam int length,
            @RequestParam(required = false) String tanggalDari,
            @RequestParam(required = false) String tanggalSampai,
            @RequestParam(required = false) String branchCode
    ) {

        int page = start / length;
        int size = length;

        // =========================================
        // DEFAULT TANGGAL
        // =========================================

        LocalDate dari;
        LocalDate sampai;

        if (tanggalDari == null || tanggalDari.isBlank()) {
            dari = LocalDate.now().withDayOfMonth(1);
        } else {
            dari = LocalDate.parse(tanggalDari);
        }

        if (tanggalSampai == null || tanggalSampai.isBlank()) {
            sampai = LocalDate.now();
        } else {
            sampai = LocalDate.parse(tanggalSampai);
        }

        // =========================================
        // SERVICE
        // =========================================

        PageResponse<NeracaSaldoDTO> response =
                neracaSaldoService.getNeracaSaldo(
                        page,
                        size,
                        dari,
                        sampai,
                        branchCode
                );

        // =========================================
        // DATATABLE RESPONSE
        // =========================================

        Map<String, Object> result = new HashMap<>();
        result.put("draw", draw);
        result.put("recordsTotal", response.getTotalElements());
        result.put("recordsFiltered", response.getTotalElements());
        result.put("data", response.getContent());

        return result;
    }

    // =========================================
    // TEST ENDPOINT
    // =========================================

//     @GetMapping("/test")
//     @ResponseBody
//     public List<NeracaSaldoProjection> testNeracaSaldo(
//             @RequestParam String tanggalDari,
//             @RequestParam String tanggalSampai,
//             @RequestParam(required = false) String branchCode
//     ) {

//         LocalDateTime dari = LocalDate.parse(tanggalDari).atStartOfDay();
//         LocalDateTime sampai = LocalDate.parse(tanggalSampai)
//                 .plusDays(1)
//                 .atStartOfDay();

//         return neracaSaldoRepository.findNeracaSaldo(
//                 dari,
//                 sampai,
//                 branchCode
//         );
//     }

    // =========================================
    // SUBMIT NERACA SALDO
    // =========================================

//     @PostMapping("/submit")
//     @ResponseBody
//     public Map<String, Object> submit(
//             @RequestParam LocalDate periode,
//             @RequestParam String branchCode
//     ) {

//         Map<String, Object> result = new HashMap<>();

//         try {
//             // =========================================
//             // VALIDASI CABANG
//             // =========================================

//             if (branchCode == null || branchCode.isBlank()) {
//                 throw new IllegalArgumentException(
//                         "Silakan pilih cabang terlebih dahulu."
//                 );
//             }

//             // =========================================
//             // CARI CABANG
//             // =========================================

//             Cabang cabang = cabangService.getByKodeCabang(branchCode);

//             // =========================================
//             // UNIT ID
//             // =========================================
//             Long unitId = cabang.getId().longValue();
//             // =========================================
//             // SUBMIT
//             // =========================================

//             String submittedBy = "SYSTEM"; // nanti bisa diseuikan

//             //if (submittedBy == null || submittedBy.trim().isEmpty()) {
//                 Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//                 if (auth != null && auth.isAuthenticated()) {
//                     Object principal = auth.getPrincipal();
//                     if (principal instanceof CustomUserPrincipal) {
//                         submittedBy = ((CustomUserPrincipal) principal).getUsername();
//                     }
//                 }
//             //}

//             NeracaSaldoSubmission submission =
//                     neracaSaldoService2.submit(
//                             periode,
//                             unitId,
//                             submittedBy
//                     );

//             // =========================================
//             // RESPONSE
//             // =========================================

//             result.put("success", true);
//             result.put("message", "Neraca Saldo berhasil dikirim untuk validasi.");
//             result.put("submissionId", submission.getId());
//             result.put("status", submission.getStatus().name());

//         } catch (Exception e) {
//             log.error("Gagal mengirim Neraca Saldo untuk validasi", e);

//             result.put("success", false);
//             result.put("message", e.getMessage());
//         }

//         return result;
//     }


        @PostMapping("/submit")
        @ResponseBody
        public Map<String, Object> submit(
                @RequestParam LocalDate periode,
                @RequestParam String branchCode
        ) {

        Map<String, Object> result = new HashMap<>();

        try {

                if (branchCode == null || branchCode.isBlank()) {
                        throw new IllegalArgumentException(
                                "Silakan pilih cabang terlebih dahulu."
                        );
                }

                // Periode Neraca Saldo selalu direpresentasikan
                // dengan tanggal pertama bulan
                LocalDate periodeBulan =  periode.withDayOfMonth(1);
                Cabang cabang =  cabangService.getByKodeCabang(branchCode);
                Long unitId =  cabang.getId().longValue();
                String submittedBy = "SYSTEM";
                NeracaSaldoSubmission submission =
                        neracaSaldoService2.submit(
                                periodeBulan,
                                unitId,
                                submittedBy
                        );

                result.put("success", true);
                result.put(
                        "message",
                        "Neraca Saldo berhasil dikirim untuk validasi."
                );
                result.put(
                        "submissionId",
                        submission.getId()
                );
                result.put(
                        "status",
                        submission.getStatus().name()
                );

        } catch (Exception e) {

                log.error(
                        "Gagal mengirim Neraca Saldo untuk validasi",
                        e
                );

                result.put("success", false);
                result.put("message", e.getMessage());
        }

        return result;
        }


        @GetMapping("/export-pdf")
        public ResponseEntity<InputStreamResource> exportPdf(
                @RequestParam(required = false) String ids,
                @RequestParam(required = false) String tanggalDari,
                @RequestParam(required = false) String tanggalSampai,
                @RequestParam(required = false) String branchCode
        ) {
                LocalDate dari = (tanggalDari == null || tanggalDari.isBlank()) 
                        ? LocalDate.now().withDayOfMonth(1) 
                        : LocalDate.parse(tanggalDari);

                LocalDate sampai = (tanggalSampai == null || tanggalSampai.isBlank()) 
                        ? LocalDate.now() 
                        : LocalDate.parse(tanggalSampai);

                List<NeracaSaldoDTO> dataList = neracaSaldoService.getAllNeracaSaldo(ids, dari, sampai, branchCode);
                ByteArrayInputStream pdfStream = pdfGenerator.generatePdf(dataList, dari, sampai, branchCode);

                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Disposition", "attachment; filename=Neraca_Saldo_" + dari + "_sd_" + sampai + ".pdf");

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(new InputStreamResource(pdfStream));
        }

        @GetMapping("/export-excel")
        public void  exportExcel(
                @RequestParam(required = false) String ids,
                @RequestParam(required = false) String tanggalDari,
                @RequestParam(required = false) String tanggalSampai,
                @RequestParam(required = false) String branchCode,
                HttpServletResponse response
        )throws IOException  {
                LocalDate dari = (tanggalDari == null || tanggalDari.isBlank()) 
                        ? LocalDate.now().withDayOfMonth(1) 
                        : LocalDate.parse(tanggalDari);

                LocalDate sampai = (tanggalSampai == null || tanggalSampai.isBlank()) 
                        ? LocalDate.now() 
                        : LocalDate.parse(tanggalSampai);

                List<NeracaSaldoDTO> items = neracaSaldoService.getAllNeracaSaldo(ids, dari, sampai, branchCode);
              
                        // 3. Set Response Header
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment; filename=neraca_saldo.xlsx");

                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Neraca Saldo");

                // Header Style
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);

                // Columns Definition
                String[] columns = {
                        "Transaction Date",
                        "Account GL",
                        "Account Name",
                        "Normal Saldo",
                        "Saldo Awal",
                        "Debit",
                        "Kredit",
                        "Saldo Akhir Debit",
                        "Saldo Akhir Kredit",
                       
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
                        for (NeracaSaldoDTO item : items) {
                                Row row = sheet.createRow(rowNum++);

                                // Tanggal
                                // if (item.getTransactionDate() != null) {
                                // row.createCell(0).setCellValue(item.getTransactionDate().format(dateFormatter));
                                // } else {
                                row.createCell(0).setCellValue("");
                                // }

                                row.createCell(1).setCellValue(item.getAccountCode() != null ? item.getAccountCode() : "");
                                row.createCell(2).setCellValue(item.getAccountName() != null ? item.getAccountName() : "");
                                row.createCell(3).setCellValue(item.getNormalBalance() != null ? item.getNormalBalance() : "");
                                double sawal = (item.getSaldoAwal() != null) ? item.getSaldoAwal().doubleValue() : 0.0;
                                row.createCell(4).setCellValue(sawal);
                                double debit = (item.getTotalDebit() != null) ? item.getTotalDebit().doubleValue() : 0.0;                               
                                row.createCell(5).setCellValue(debit);
                                double kredit = (item.getTotalKredit() != null) ? item.getTotalKredit().doubleValue() : 0.0;                               
                                row.createCell(6).setCellValue(kredit);
                                double sakhirDe = (item.getSaldoAkhirDebit() != null) ? item.getSaldoAkhirDebit().doubleValue() : 0.0;
                                row.createCell(7).setCellValue(sakhirDe);
                                double sakhirKr = (item.getSaldoAkhirKredit() != null) ? item.getSaldoAkhirKredit().doubleValue() : 0.0;
                                row.createCell(8).setCellValue(sakhirKr);
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