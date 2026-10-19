package com.project.gl.controller.parameter;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.IOException;
import com.project.gl.controller.BaseController;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.GlAccountType;
import com.project.gl.model.response.PageResponse;
import com.project.gl.repo.NomorGlAccountRepository;
import com.project.gl.service.GlAccountTypeService;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("/parameter/gl-type-account") 
public class GlTypeController extends BaseController{

    @Autowired
    private NomorGlAccountRepository nomorGlAccountRepository;

    @Autowired
    private GlAccountTypeService service;


    @GetMapping
    public String index(Model model) {
        model.addAttribute("breadcrumbItems", new String[]{"PARAMETER", "GL TYPE ACCOUNT" });
        setActiveMenu(model, "gl-type-account", "master");    
        return "parameter/gl-type-account/index";
    }
    
    
    // Untuk DataTables
    @GetMapping("/datatables")
    @ResponseBody
    public Map<String, Object> getDataTables(
            @RequestParam int draw,
            @RequestParam int start,
            @RequestParam int length,
            @RequestParam int orderColumn,
            @RequestParam String orderDir,
            @RequestParam(required = false) String name
           
    ) {
        int page = start / length;
        int size = length;
        String sortBy = getSortColumn(orderColumn);
        
        // if (searchValue != null && !searchValue.isEmpty()) {
        //     if ((name == null || name.isEmpty()) && (note == null || note.isEmpty())) {
        //         name = searchValue;
        //         note = searchValue;
        //     }
        // }
        
        PageResponse<GlAccountType> response = service.getAllWithFilter(
                page, size, sortBy, orderDir, name);
        
        Map<String, Object> result = new HashMap<>();
        result.put("draw", draw);
        result.put("recordsTotal", response.getTotalElements());
        result.put("recordsFiltered", response.getTotalElements());
        result.put("data", response.getContent());
        
        return result;
    }

    // Untuk dropdown/select option
    @GetMapping("/list")
    public ResponseEntity<PageResponse<GlAccountType>> getList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    ) {
        PageResponse<GlAccountType> response = service.getAllWithFilter(
                page, size, "id", "ASC", null);
        return ResponseEntity.ok(response);
    }

    // @GetMapping("/list")
    // public ResponseEntity<PageResponse<GlAccountType>> getListOption(
    //         @RequestParam(defaultValue = "0") int page,
    //         @RequestParam(defaultValue = "100") int size
    // ) {
    //     PageResponse<Cabang> response = service.getAllWithFilter(
    //             page, size, "namaCabang", "ASC", null, null);
    //     return ResponseEntity.ok(response);
    // }



   @GetMapping("/gl-type-account/list-get-all")
    public ResponseEntity<PageResponse<GlAccountType>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String orderDir,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String note
    ) {
        PageResponse<GlAccountType> response = service.getAllWithFilter(
                page, size, sortBy, orderDir, name);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<GlAccountType> getById(@PathVariable Integer id) {
        GlAccountType response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<GlAccountType> create(@RequestBody GlAccountType request) {
        GlAccountType response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<GlAccountType> update(
            @PathVariable Integer id,
            @RequestBody GlAccountType request
    ) {
        GlAccountType response = service.update(id, request);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            // Cek apakah kategori digunakan di tabel lain (contoh: nomor_gl_account)
            Boolean isUsed = nomorGlAccountRepository.existsByTypeGlAccountId(id.longValue());
            
            if (isUsed) {
                // Kembalikan response dengan pesan error
                return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("Type tidak dapat dihapus karena sedang digunakan di tabel lain!");
            }
            
            // Jika tidak digunakan, hapus data
            service.delete(id);
            return ResponseEntity.ok().body("Data berhasil dihapus");
            
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private String getSortColumn(int orderColumn) {
        switch (orderColumn) {
            case 1: return "name";
            case 2: return "note";
            case 3: return "createdAt";
            case 4: return "updatedAt";
            default: return "id";
        }
    }

    @GetMapping("/export-excel")
    public void  exportExcel(
                @RequestParam(required = false) String ids,
                @RequestParam(required = false) String kaegori,
                HttpServletResponse response
                )throws IOException  {
              
                List<GlAccountType> items = service.getAllWithFilterExport(ids,kaegori);
               // 3. Set Response Header
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment; filename=gl-type.xlsx");

                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("GL TYPE");

                // Header Style
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);

                // Columns Definition
                String[] columns = {
                        "NAMA",
                        "NOTE"
                       
                };


                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < columns.length; i++) {
                        Cell cell = headerRow.createCell(i);
                        cell.setCellValue(columns[i]);
                        cell.setCellStyle(headerStyle);
                }
                int rowNum = 1;
             
                 if (items != null) {
                        for (GlAccountType item : items) {
                                Row row = sheet.createRow(rowNum++);
                                row.createCell(0).setCellValue(item.getName() != null ? item.getName() : "");
                                row.createCell(1).setCellValue(item.getNote() != null ? item.getNote() : "");
                              
                              
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
