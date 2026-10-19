package com.project.gl.controller.user;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.project.gl.model.response.PageResponse;
import com.project.gl.controller.BaseController;
import com.project.gl.model.entity.ApiClient;
import com.project.gl.model.entity.KodeTransaksi;
import com.project.gl.model.entity.UserGroup;
import com.project.gl.model.response.ApiClientResponse;
import com.project.gl.model.response.ClientKeyResponse;
import com.project.gl.model.response.KodeTransaksiResponse;
import com.project.gl.service.ApiClientService;

import jakarta.validation.Valid;


@Controller
@RequestMapping("/management/api-client")
public class ApiClientController extends  BaseController{

    @Autowired
    private ApiClientService apiClientService;

    
    @GetMapping
    public String index(Model model) {
        
        model.addAttribute("breadcrumbItems", new String[]{"Console", "Management Key Secret API Client"});
        setActiveMenu(model, "api-client", "management");  
        return "management/api_client/index";
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
            @RequestParam(required = false) String client_name,
            @RequestParam(required = false) String client_id,
            @RequestParam(required = false) Boolean active    
        ) {
         
        int page = start / length;
        int size = length;
        String sortBy = getSortColumn(orderColumn);
      
        PageResponse<ApiClientResponse> response = apiClientService.getAllApiClient(
            page, size, sortBy, orderDir, client_name, client_id, active);
       
        
        Map<String, Object> result = new HashMap<>();
        result.put("draw", draw);
        result.put("recordsTotal", response.getTotalElements());
        result.put("recordsFiltered", response.getTotalElements());
        result.put("data", response.getContent());
        
        return result;
    }

     private String getSortColumn(int orderColumn) {
        switch(orderColumn) {
            case 1: return "client_name";
            case 2: return "client_id";
            // case 3: return "city";
            // case 4: return "phone";
            // case 5: return "email";
            default: return "id";
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ApiClient> getById(@PathVariable Long id) {
        ApiClient response = apiClientService.findById(id);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> create(@Valid @RequestBody ApiClient request) {
        try {
            ClientKeyResponse response = apiClientService.createApi(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/regenerate-secret/{id}")
    @ResponseBody
    public ResponseEntity<?> regenerateSecret(@PathVariable Long id) {
        try {
            ClientKeyResponse response = apiClientService.regenerate(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

   
    @PostMapping("/aktif-non-aktif/{id}")
    public ResponseEntity<?> aktifNonAktif(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String type = body.get("type");
            apiClientService.aktifNonAktif(id, type);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Berhasil " + type.toLowerCase()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

   

    @PostMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ApiClient request) {
       try {
            apiClientService.update(id, request);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Update Berhasil" 
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    
}
