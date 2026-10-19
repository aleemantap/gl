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
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.GlAccountType;
import com.project.gl.model.entity.NomorGlAccount;
import com.project.gl.model.request.NomorGlAccountRequest;
import com.project.gl.model.response.LogKustodiKasDTO;
import com.project.gl.model.response.NomorGelAccountResponse;
import com.project.gl.model.response.PageResponse;
import com.project.gl.service.CabangService;
import com.project.gl.service.GlAccountTypeService;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

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
@RequestMapping("/parameter/cabang") 
public class CabangController extends BaseController {

    @Autowired
    private CabangService service;

    @GetMapping
    public String dashboard(Model model) {
               
        model.addAttribute("breadcrumbItems", new String[]{"PARAMETER","CABANG" });
        setActiveMenu(model, "cabang", "master");    
        return "parameter/cabang/index";
    }

    @GetMapping("/datatables")
    @ResponseBody
    public Map<String, Object> getDataTables(
            @RequestParam int draw,
            @RequestParam int start,
            @RequestParam int length,
            @RequestParam int orderColumn,
            @RequestParam String orderDir,
            @RequestParam(required = false) String kodeCabang,
            @RequestParam(required = false) String namaCabang
    ) {
        int page = start / length;
        int size = length;
        String sortBy = getSortColumn(orderColumn);        
        PageResponse<Cabang> response = service.getAllWithFilter(page, size, sortBy, orderDir, kodeCabang, namaCabang);        
        Map<String, Object> result = new HashMap<>();
        result.put("draw", draw);
        result.put("recordsTotal", response.getTotalElements());
        result.put("recordsFiltered", response.getTotalElements());
        result.put("data", response.getContent());        
        return result;
    }

  

    @GetMapping("/list")
    public ResponseEntity<PageResponse<Cabang>> getList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    ) {
        PageResponse<Cabang> response = service.getAllWithFilter(
                page, size, "namaCabang", "ASC", null, null);
        return ResponseEntity.ok(response);
    }

   

    @GetMapping("/get/{id}")
    public ResponseEntity<Cabang> getById(@PathVariable Integer id) {
        Cabang response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    @ResponseBody
    public Map<String, Object> saveJson(@RequestBody Cabang request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Cabang response = service.create(request);
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
    public ResponseEntity<Cabang> update(
            @PathVariable Integer id,
            @RequestBody Cabang request
    ) {
        Cabang response = service.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private String getSortColumn(int orderColumn) {
        switch (orderColumn) {
            case 1: return "kodeCabang";
            case 2: return "namaCabang";
            default: return "id";
        }
    }

    @GetMapping("/export-excel")
    public void  exportExcel(
                @RequestParam(required = false) String ids,
                @RequestParam(required = false) String kode,
                @RequestParam(required = false) String cabang,
                HttpServletResponse response
                )throws IOException  {
              
                List<Cabang> items = service.getAllWithFilterExport(ids,kode,cabang);
               // 3. Set Response Header
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment; filename=cabang.xlsx");

                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("CABANG");

                // Header Style
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);

                // Columns Definition
                String[] columns = {
                        "KODE",
                        "NAMA",
                        "ALAMAT"
                       
                };


                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < columns.length; i++) {
                        Cell cell = headerRow.createCell(i);
                        cell.setCellValue(columns[i]);
                        cell.setCellStyle(headerStyle);
                }
                int rowNum = 1;
             
                 if (items != null) {
                        for (Cabang item : items) {
                                Row row = sheet.createRow(rowNum++);
                                row.createCell(0).setCellValue(item.getKodeCabang() != null ? item.getKodeCabang() : "");
                                row.createCell(1).setCellValue(item.getNamaCabang() != null ? item.getNamaCabang() : "");
                                row.createCell(2).setCellValue(item.getAlamatLengkap() != null ? item.getAlamatLengkap() : "");
                              
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
