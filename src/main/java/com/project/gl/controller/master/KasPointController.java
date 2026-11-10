package com.project.gl.controller.master;
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
import com.project.gl.model.entity.KasPoint;
import com.project.gl.model.entity.NomorGlAccount;
import com.project.gl.model.request.NomorGlAccountRequest;
import com.project.gl.model.response.KasPointResponse;
import com.project.gl.model.response.LogKustodiKasDTO;
import com.project.gl.model.response.NomorGelAccountResponse;
import com.project.gl.model.response.PageResponse;
import com.project.gl.service.CabangService;
import com.project.gl.service.GlAccountTypeService;
import com.project.gl.service.KasPointService;

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
@RequestMapping("/master/kas-point") 
public class KasPointController extends BaseController {

    @Autowired
    private CabangService serviceCabang;

    @Autowired
    private KasPointService serviceKasPoint;

    @GetMapping
    public String index(Model model) {
               
        model.addAttribute("breadcrumbItems", new String[]{"Console","Master","Kas Point" });
        setActiveMenu(model, "kas-point", "master"); 
        List<Cabang> cabangList = serviceCabang.getAll(); 
        model.addAttribute("cabangList", cabangList);   
        model.addAttribute("jenisList", List.of(
            Map.of("value", "teller",    "label", "Teller"),
            Map.of("value", "kas_kecil", "label", "Kas Kecil"),
            Map.of("value", "brankas",   "label", "Brankas")
        ));
        return "master/kas-point/index";
    }

      /* ===== AJAX: list (untuk DataTables) ===== */
    @GetMapping("/data")
    @ResponseBody
    public List<KasPointResponse> data(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String nama,
            @RequestParam(required = false) String jenis,
            @RequestParam(required = false) Integer cabangId) {
        return serviceKasPoint.findAll(code, nama, jenis, cabangId);
    }

    /* ===== AJAX: get by id (untuk edit) ===== */
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<KasPoint> getById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceKasPoint.findById(id));
    }

    /* ===== AJAX: create ===== */
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> create(@RequestBody KasPoint kasPoint) {
        KasPoint saved = serviceKasPoint.save(kasPoint);
        return ResponseEntity.ok(Map.of("success", true, "data", saved));
    }

    /* ===== AJAX: update ===== */
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody KasPoint kasPoint) {
        KasPoint updated = serviceKasPoint.update(id, kasPoint);
        return ResponseEntity.ok(Map.of("success", true, "data", updated));
    }

    /* ===== AJAX: delete ===== */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable Long id) {
        serviceKasPoint.delete(id);
        return ResponseEntity.ok(Map.of("success", true));
    }


}
