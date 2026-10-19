package com.project.gl.controller.parameter;

import com.project.gl.model.request.ComponentRequest;
import com.project.gl.model.response.PageResponse;
import com.project.gl.controller.BaseController;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.Component;
import com.project.gl.service.CabangService;
import com.project.gl.service.ComponentService;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/parameter/komponen") 
public class ComponentController extends BaseController {
    
   
    @Autowired
    private ComponentService componentService;    
   
    
    @GetMapping
    public String index(Model model) {
        setActiveMenu(model, "komponen", "master");        
        model.addAttribute("breadcrumbItems", new String[]{"PARAMETER","COMPONENT TRANSACTIONS" });
        return "parameter/komponen/index";
    }
    
    // API endpoint untuk DataTable
    @GetMapping("/datatables")
    @ResponseBody
    public Map<String, Object> getDataTables(
            @RequestParam int draw,
            @RequestParam int start,
            @RequestParam int length,
            @RequestParam int orderColumn,
            @RequestParam String orderDir,
            @RequestParam(required = false) String componentCode,
            @RequestParam(required = false) String componentName
    ) {
        int page = start / length;
        int size = length;
        String sortBy = getSortColumn(orderColumn);
        
        PageResponse<Component> response = componentService.getAllWithFilter(
                page, size, sortBy, orderDir, componentCode, componentName
        );
        
        Map<String, Object> result = new HashMap<>();
        result.put("draw", draw);
        result.put("recordsTotal", response.getTotalElements());
        result.put("recordsFiltered", response.getTotalElements());
        result.put("data", response.getContent());
        
        return result;
    }

     // Helper method untuk mapping sort column
    private String getSortColumn(int orderColumn) {
        switch (orderColumn) {
            case 0: return "id";
            case 1: return "componentCode";
            case 2: return "componentName";
            case 3: return "description";
            case 4: return "isActive";
            case 5: return "createdAt";
            default: return "id";
        }
    }
    
   
    
    // Proses tambah data
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> create(
            @RequestBody ComponentRequest request,
            Principal principal
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = principal != null ? principal.getName() : "system";
            Component created = componentService.create(request, username);
            response.put("success", true);
            response.put("message", "Component created successfully");
            response.put("data", created);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getComponent(@PathVariable Long id) {
        try {
            Component component = componentService.getById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", component);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
     // Update component via AJAX
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable Long id,
            @RequestBody ComponentRequest request,
            Principal principal
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = principal != null ? principal.getName() : "system";
            Component updated = componentService.update(id, request, username);
            response.put("success", true);
            response.put("message", "Component updated successfully");
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/export-excel")
    public void  exportExcel(
                @RequestParam(required = false) String ids,
                @RequestParam(required = false) String nama,
                @RequestParam(required = false) String kode,
                HttpServletResponse response
                )throws IOException  {
              
                List<Component> items = componentService.getAllWithFilterExport(ids,nama,kode);
               // 3. Set Response Header
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment; filename=komponen.xlsx");

                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("komponen");

                // Header Style
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);

                // Columns Definition
                String[] columns = {
                        "KODE",
                        "NAMA",
                        "Description"
                       
                };


                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < columns.length; i++) {
                        Cell cell = headerRow.createCell(i);
                        cell.setCellValue(columns[i]);
                        cell.setCellStyle(headerStyle);
                }
                int rowNum = 1;
             
                 if (items != null) {
                        for (Component item : items) {
                                Row row = sheet.createRow(rowNum++);
                                row.createCell(0).setCellValue(item.getComponentCode() != null ? item.getComponentCode() : "");
                                row.createCell(1).setCellValue(item.getComponentName() != null ? item.getComponentName() : "");
                                row.createCell(2).setCellValue(item.getDescription() != null ? item.getDescription() : "");
                              
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
    
      // Delete component via AJAX
    // @DeleteMapping("/{id}")
    // @ResponseBody
    // public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
    //     Map<String, Object> response = new HashMap<>();
    //     try {
    //         componentService.delete(id);
    //         response.put("success", true);
    //         response.put("message", "Component deleted successfully");
    //         return ResponseEntity.ok(response);
    //     } catch (Exception e) {
    //         response.put("success", false);
    //         response.put("message", e.getMessage());
    //         return ResponseEntity.badRequest().body(response);
    //     }
    // }
}