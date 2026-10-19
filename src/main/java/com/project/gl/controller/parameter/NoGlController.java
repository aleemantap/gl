package com.project.gl.controller.parameter;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.project.gl.service.CabangService;
import com.project.gl.service.GlAccountTypeService;
import com.project.gl.controller.BaseController;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.GlAccountType;
import com.project.gl.model.entity.NomorGlAccount;
import com.project.gl.model.request.NomorGlAccountRequest;
import com.project.gl.model.request.NomorGlFilterRequest;
import com.project.gl.model.response.KodeTransaksiResponse;
import com.project.gl.model.response.NomorGelAccountResponse;
import com.project.gl.model.response.PageResponse;
import com.project.gl.repo.JournalDetailRepository;
import com.project.gl.service.NomorGlAccountService;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
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
@RequestMapping("/parameter/nomor-gl") 
public class NoGlController extends BaseController{

    @Autowired
    private NomorGlAccountService service;  

    @Autowired
    private JournalDetailRepository journalDetailRepository;  

    @Autowired
    private GlAccountTypeService kategoriService;  

    @GetMapping
    public String dashboard(Model model) {

        setActiveMenu(model, "nomor-gl", "parameter");       
        model.addAttribute("breadcrumbItems", new String[]{"PARAMETER / NOMOR GL (CHART OF ACCOUNTS)", });
        //List<Cabang> cabangList = cabangService.getAll(); 
        //model.addAttribute("cabangList", cabangList);
        List<GlAccountType> glTypeList = kategoriService.getAll(); 
        model.addAttribute("glTypeList", glTypeList);

        return "parameter/nogel/index";
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
            // @RequestParam(required = false) String nomorGl,
            // @RequestParam(required = false) String namaGlAccount,
            // @RequestParam(required = false) Integer kategoriId,
            // @RequestParam(required = false) Integer cabangId,
            // @RequestParam(required = false) String debitKredit
             @ModelAttribute NomorGlFilterRequest filter
    ) {
        int page = start / length;
        int size = length;
        String sortBy = getSortColumn(orderColumn);
        
        
        
        PageResponse<NomorGelAccountResponse> response = service.getAllWithFilter(
                page, size, sortBy, orderDir, filter.getNomorGl(), filter.getNamaGlAccount(), 
                filter.getTypeId(), filter.getSaldoNormal());
        
        
        Map<String, Object> result = new HashMap<>();
        result.put("draw", draw);
        result.put("recordsTotal", response.getTotalElements());
        result.put("recordsFiltered", response.getTotalElements());
        result.put("data", response.getContent());
        
        return result;
    }

    // Alternative DataTables endpoint with better filter handling
    // @GetMapping("/datatables-v2")
    // @ResponseBody
    // public Map<String, Object> getDataTablesV2(
    //         @RequestParam int draw,
    //         @RequestParam int start,
    //         @RequestParam int length,
    //         @RequestParam(required = false) String search,
    //         @RequestParam(required = false) String nomorGl,
    //         @RequestParam(required = false) String namaGlAccount,
    //         @RequestParam(required = false) Integer kategoriId,
    //         @RequestParam(required = false) Integer cabangId,
    //         @RequestParam(required = false) String debitKredit
    // ) {
    //     int page = start / length;
    //     int size = length;
        
    //     // Use search param as fallback
    //     if (search != null && !search.isEmpty()) {
    //         if ((nomorGl == null || nomorGl.isEmpty()) && 
    //             (namaGlAccount == null || namaGlAccount.isEmpty())) {
    //             nomorGl = search;
    //             namaGlAccount = search;
    //         }
    //     }
        
    //     PageResponse<NomorGelAccountResponse> response = service.getAllWithFilter(
    //             page, size, "id", "ASC", nomorGl, namaGlAccount, 
    //             kategoriId, cabangId, debitKredit);
        
    //     Map<String, Object> result = new HashMap<>();
    //     result.put("draw", draw);
    //     result.put("recordsTotal", response.getTotalElements());
    //     result.put("recordsFiltered", response.getTotalElements());
    //     result.put("data", response.getContent());
        
    //     return result;
    // }

    // CRUD Endpoints
    @GetMapping("/get-all")
    public ResponseEntity<PageResponse<NomorGelAccountResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String orderDir,
            @RequestParam(required = false) String nomorGl,
            @RequestParam(required = false) String namaGlAccount,
            @RequestParam(required = false) Integer typeId,
            // @RequestParam(required = false) Integer cabangId,
            @RequestParam(required = false) String saldoNormal
    ) {
        PageResponse<NomorGelAccountResponse> response = service.getAllWithFilter(
                page, size, sortBy, orderDir, nomorGl, namaGlAccount, 
                typeId,  saldoNormal);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NomorGelAccountResponse> getById(@PathVariable Integer id) {
        NomorGelAccountResponse response = service.getById(id);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/by-nomor-gl")
    public ResponseEntity<NomorGelAccountResponse> getByNomorGl(@RequestParam String nomorGl) {
        NomorGelAccountResponse response = service.getByNomorGl(nomorGl);
        return ResponseEntity.ok(response);
    }

    // @PostMapping("/add")
    // @ResponseBody
    // public Map<String, Object> saveJson(@RequestBody NomorGlAccount request) {
    //     Map<String, Object> result = new HashMap<>();
        
    //     try {
    //         NomorGelAccountResponse response = service.create(request);
    //         result.put("success", true);
    //         result.put("message", "Data berhasil disimpan!");
    //         result.put("data", response);
    //     } catch (Exception e) {
    //         result.put("success", false);
    //         result.put("message", e.getMessage());
    //     }
        
    //     return result;
    // }

    @PostMapping("/add")
    @ResponseBody
    public Map<String, Object> saveJson(@RequestBody NomorGlAccountRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            NomorGelAccountResponse response = service.create(request);
            result.put("success", true);
            result.put("message", "Data berhasil disimpan!");
            result.put("data", response);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        
        return result;
    }



    @PostMapping("/update/{id}")
    @ResponseBody
    public Map<String, Object> ubahJson( @PathVariable Integer id, @RequestBody NomorGlAccountRequest request) 
    {
        Map<String, Object> result = new HashMap<>();
        try {
            NomorGelAccountResponse response =  service.update(id, request);
            result.put("success", true);
            result.put("message", "Data berhasil diubah!");
            result.put("data", response);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        
        return result;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteJson(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean isUsed = journalDetailRepository.existsByGlAccountId(id.longValue());

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
    // Helper method to map order column index to field name
    private String getSortColumn(int orderColumn) {
        switch (orderColumn) {
            case 1: return "nomorGl";
            // case 2: return "namaGlAccount";
            // case 3: return "kategori";
            // case 4: return "cabang";
            // case 5: return "debitKredit";
            // case 6: return "createdAt";
            // case 7: return "updatedAt";
            default: return "id";
        }
    }

     @GetMapping("/export-excel")
        public void  exportExcel(
                @RequestParam(required = false) String ids,
                @RequestParam(required = false) String nomorGl,
                @RequestParam(required = false) String namaGlAccount,
                @RequestParam(required = false) Integer typeId,
                @RequestParam(required = false) String saldoNormal,
                HttpServletResponse response) throws IOException  {
                // LocalDate dari = (tanggalDari == null || tanggalDari.isBlank()) 
                //         ? LocalDate.now().withDayOfMonth(1) 
                //         : LocalDate.parse(tanggalDari);

                // LocalDate sampai = (tanggalSampai == null || tanggalSampai.isBlank()) 
                //         ? LocalDate.now() 
                //         : LocalDate.parse(tanggalSampai);

                List<NomorGelAccountResponse> items = service.getAllWithFilterExport(ids, nomorGl, namaGlAccount,typeId,saldoNormal);
               
                        // 3. Set Response Header
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment; filename=kode_nomor_GL_akun.xlsx");

                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("GL AKUN");

                // Header Style
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);

                // Columns Definition
                String[] columns = {
                        "KODE GL",
                        "NAMA AKUN GL",
                        "TYPE GL",
                        "SALDO NORMAL"
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
                        for (NomorGelAccountResponse item : items) {
                                Row row = sheet.createRow(rowNum++);

                                // Tanggal
                                // if (item.getTransactionDate() != null) {
                                // row.createCell(0).setCellValue(item.getTransactionDate().format(dateFormatter));
                                // } else {
                                // row.createCell(0).setCellValue("");
                                // }

                                row.createCell(0).setCellValue(item.getNomorGl() != null ? item.getNomorGl() : "");
                                row.createCell(1).setCellValue(item.getNamaGlAccount() != null ? item.getNamaGlAccount() : "");
                                row.createCell(2).setCellValue(item.getGlTypeName() != null ? item.getGlTypeName() : "");
                                row.createCell(3).setCellValue(item.getSaldoNormal() != null ? item.getSaldoNormal() : "");
                                
                                
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
