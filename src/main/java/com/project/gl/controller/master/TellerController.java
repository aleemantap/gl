package com.project.gl.controller.master;

import com.project.gl.model.response.TellerResponse;
import com.project.gl.model.entity.Teller;
import com.project.gl.service.TellerService;
import com.project.gl.controller.BaseController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/master/teller")
public class TellerController extends BaseController{

    @Autowired
    private TellerService service;


    /* ===== Halaman ===== */
    @GetMapping
    public String index(Model model) {
        model.addAttribute("title", "Teller");
        model.addAttribute("breadcrumbItems", new String[]{"Console","Master","Teller" });
        setActiveMenu(model, "teller", "master"); 
      
        return "master/teller/index";
    }

    /* ===== AJAX: list dengan filter ===== */
    @GetMapping("/data")
    @ResponseBody
    public List<TellerResponse> data(
            @RequestParam(required = false) String nama,
            @RequestParam(required = false) String userGadai) {
        return service.findAll(nama, userGadai);
    }

    /* ===== AJAX: get by id ===== */
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Teller> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    /* ===== AJAX: create ===== */
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> create(@RequestBody Teller teller) {
        return ResponseEntity.ok(Map.of("success", true, "data", service.save(teller)));
    }

    /* ===== AJAX: update ===== */
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Teller teller) {
        return ResponseEntity.ok(Map.of("success", true, "data", service.update(id, teller)));
    }

    /* ===== AJAX: delete ===== */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(Map.of("success", true));
    }
}