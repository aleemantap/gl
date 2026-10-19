package com.project.gl.controller.parameter;


import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.gl.controller.BaseController;
import com.project.gl.model.entity.CategoryTransaction;
import com.project.gl.model.entity.KodeTransaksi;
import com.project.gl.model.response.KodeTransaksiResponse;
import com.project.gl.model.response.NeracaSaldoDTO;
import com.project.gl.model.response.PageResponse;
import com.project.gl.service.KodeTransaksiService;
import com.project.gl.service.NomorGlAccountService;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import com.project.gl.repo.JournalKodeTransaksiRepository;

import java.io.IOException;
import java.time.LocalDate;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/parameter/kode-transaksi") 
public class KodeTransaksiController extends BaseController {

    @Autowired
    private KodeTransaksiService service;  

    @Autowired
    private JournalKodeTransaksiRepository journalKodeTransaksiRepository;

    @GetMapping
    public String dashboard(Model model) {
        setActiveMenu(model, "kode-transaksi ", "parameter");             
        model.addAttribute("breadcrumbItems", new String[]{"PARAMETER","KODE TRANSAKSI" });
        return "parameter/kodeTransaksi/index";
    }

    @GetMapping("/datatables")
    @ResponseBody
    public Map<String, Object> getDataTables(
            @RequestParam int draw,
            @RequestParam int start,
            @RequestParam int length,
            @RequestParam int orderColumn,
            @RequestParam String orderDir,
            // @RequestParam(required = false) String searchValue,
            @RequestParam(required = false) String kode,
            @RequestParam(required = false) String nama
    ) {
        int page = start / length;
        int size = length;
        String sortBy = getSortColumn(orderColumn);
        
        // if (searchValue != null && !searchValue.isEmpty()) {
        //     if ((kode == null || kode.isEmpty()) && (namaTransaksi == null || namaTransaksi.isEmpty())) {
        //         kode = searchValue;
        //         namaTransaksi = searchValue;
        //     }
        // }
        
        PageResponse<KodeTransaksiResponse> response = service.getAllWithFilterDT(
                page, size, sortBy, orderDir, kode, nama);
        
        Map<String, Object> result = new HashMap<>();
        result.put("draw", draw);
        result.put("recordsTotal", response.getTotalElements());
        result.put("recordsFiltered", response.getTotalElements());
        result.put("data", response.getContent());
        
        return result;
    }

    private String getSortColumn(int orderColumn) {
        switch (orderColumn) {
            case 1: return "code";
            case 2: return "name";
            case 3: return "createdAt";
            case 4: return "updatedAt";
            default: return "id";
        }
    }
 
    @GetMapping("/{id}")
    public ResponseEntity<KodeTransaksiResponse> getById(@PathVariable Integer id) {
        KodeTransaksiResponse response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-kode")
    public ResponseEntity<KodeTransaksiResponse> getByKode(@RequestParam String kode) {
        KodeTransaksiResponse response = service.getByKode(kode);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<KodeTransaksiResponse> create(@RequestBody KodeTransaksi request) {
        KodeTransaksiResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<KodeTransaksiResponse> update(
            @PathVariable Integer id,
            @RequestBody KodeTransaksi request
    ) {
        KodeTransaksiResponse response = service.update(id, request);
        return ResponseEntity.ok(response);
    }

    // @DeleteMapping("/{id}")
    // public ResponseEntity<Void> delete(@PathVariable Integer id) {
    //     //journalKodeTransaksiRepository
    //     service.delete(id);
    //     return ResponseEntity.noContent().build();
    // }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteJson(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean isUsed = journalKodeTransaksiRepository.existsByKodeTransaksiId(id.longValue());

            if (isUsed) {
                result.put("success", false);
                result.put("message", "Data gagal dihapus karena sedang digunakan di tabel lain");
                result.put("data", "Data sedang dipakai di tabel Journal Detail");
                // Tetap return 200 OK
                return ResponseEntity.ok(result);
            } else {
                service.delete(id);
                result.put("success", true);
                result.put("message", "Data berhasil dihapus!");
                result.put("data", null);
                return ResponseEntity.ok(result);
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Terjadi kesalahan: " + e.getMessage());
            result.put("data", null);
            // Tetap return 200 OK
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    // @GetMapping("/list")
    // public ResponseEntity<PageResponse<KodeTransaksi>> getList(
    //         @RequestParam(defaultValue = "0") int page,
    //         @RequestParam(defaultValue = "100") int size
    // ) {
    //     PageResponse<KodeTransaksi> response = service.getAll(
    //             page, size, "id", "ASC", null, null);
    //     return ResponseEntity.ok(response);
    // }

    @GetMapping("/list")
    public ResponseEntity<List<KodeTransaksiResponse>> getAll() {
        List<KodeTransaksiResponse> list = service.getAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/export-excel")
        public void  exportExcel(
                @RequestParam(required = false) String ids,
                @RequestParam(required = false) String kode,
                @RequestParam(required = false) String nama,
                // @RequestParam(required = false) String branchCode,
                HttpServletResponse response
        )throws IOException  {
                // LocalDate dari = (tanggalDari == null || tanggalDari.isBlank()) 
                //         ? LocalDate.now().withDayOfMonth(1) 
                //         : LocalDate.parse(tanggalDari);

                // LocalDate sampai = (tanggalSampai == null || tanggalSampai.isBlank()) 
                //         ? LocalDate.now() 
                //         : LocalDate.parse(tanggalSampai);

                List<KodeTransaksiResponse> items = service.getAllWithFilter(ids, kode, nama);
                // .getAllNeracaSaldo(ids, dari, sampai, branchCode);
              
                        // 3. Set Response Header
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment; filename=kode_transaksi.xlsx");

                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Kode Transaksi");

                // Header Style
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);

                // Columns Definition
                String[] columns = {
                        "KODE",
                        "NAMA",
                      
                       
                };

                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < columns.length; i++) {
                        Cell cell = headerRow.createCell(i);
                        cell.setCellValue(columns[i]);
                        cell.setCellStyle(headerStyle);
                }
                int rowNum = 1;
                // DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

                 if (items != null) {
                        for (KodeTransaksiResponse item : items) {
                                Row row = sheet.createRow(rowNum++);

                                // Tanggal
                                // if (item.getTransactionDate() != null) {
                                // row.createCell(0).setCellValue(item.getTransactionDate().format(dateFormatter));
                                // } else {
                                // row.createCell(0).setCellValue("");
                                // }

                                row.createCell(0).setCellValue(item.getKode() != null ? item.getKode() : "");
                                row.createCell(1).setCellValue(item.getNamaTransaksi() != null ? item.getNamaTransaksi() : "");
                                
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
