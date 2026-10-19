package com.project.gl.controller.parameter;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.Component;
import com.project.gl.model.entity.Product;
import com.project.gl.model.projection.ProductRuleComponentProjection2;
import com.project.gl.model.request.ProductRequest;
import com.project.gl.model.response.PageResponse;
import com.project.gl.model.response.ProductDataTableResponse;
import com.project.gl.model.response.ProductResponse;
import com.project.gl.model.response.ProductRuleComponentDTO;
import com.project.gl.model.response.ProductRuleDTO;
import com.project.gl.model.response.ProductRuleJournalDTO;
import com.project.gl.repo.ComponentRepository;
// import com.project.gl.model.response.ProductRuleDTO;
import com.project.gl.service.ProductService;
import com.project.gl.service.ProductRuleService;
// import com.project.gl.service.ProductRuleCompoenentService;
import com.project.gl.service.ProductRuleComponentService;
// import com.project.gl.service.ProductRuleJournalService;
import com.project.gl.service.ComponentService;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/parameter/aturan-produk-detail") 
public class AturanProdukDetailController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRuleService productRuleService;

    @Autowired
    private ProductRuleComponentService service; 

    @Autowired
    private ComponentRepository componentRepository;
    
    

    @GetMapping("/{id}/{kode}")
    public String detail(Model model, @PathVariable Long id, @PathVariable String kode) {
        Product nama = productService.getProductEntityById(id);
        model.addAttribute("breadcrumbItems", new String[]{"PARAMETER", "ATURAN PRODUK", "KODE PRODUK : "+kode,"Nama Product : "+nama.getProductName() });
        model.addAttribute("productId", id);
        model.addAttribute("namaProduk", nama.getProductName());
        List<Component> componentList = componentRepository.findAll();
        model.addAttribute("componentList", componentList);
        return "parameter/aturanProdukDetail/index";
    }

  
    @GetMapping("/datatables")
    @ResponseBody
    public Map<String, Object> getDataTables(
            @RequestParam int draw,
            @RequestParam int start,
            @RequestParam int length,
            @RequestParam int orderColumn,
            @RequestParam String orderDir,
            //@RequestParam(required = false) String approvalStatus,           
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Long productId
           
        ) {
        
        int page = start / length;
        String sortBy = getSortColumn(orderColumn);
        
        Page<ProductRuleDTO> rulePage = productRuleService.getProductRulesWithFilter(
            page, length, sortBy, orderDir, productId, isActive, search);
        
        Map<String, Object> result = new HashMap<>();
        result.put("draw", draw);
        result.put("recordsTotal", rulePage.getTotalElements());
        result.put("recordsFiltered", rulePage.getTotalElements());
        result.put("data", rulePage.getContent());
        
        return result;
    }
    
    private String getSortColumn(int orderColumn) {
        switch (orderColumn) {
            case 0: return "id";
            case 1: return "productId";
            case 2: return "ruleVersion";
            case 3: return "effectiveFrom";
            case 4: return "effectiveTo";
            case 5: return "interestRate";
            case 6: return "interestPeriodDays";
            case 7: return "interestMethod";
            case 8: return "isActive";
            case 9: return "approvalStatus";
            case 10: return "sourceSystem";
            case 11: return "createdAt";
            case 12: return "createdBy";
            default: return "id";
        }
    }

    // @PostMapping("/product-rules/save")
    // @ResponseBody
    // public ResponseEntity<Map<String, Object>> saveProductRule(@RequestBody ProductRuleDTO productRuleDTO) {
    //     Map<String, Object> response = new HashMap<>();
    //     try {
    //         // Validate
    //         if (productRuleDTO.getProductId() == null) {
    //             response.put("success", false);
    //             response.put("message", "Product is required");
    //             return ResponseEntity.badRequest().body(response);
    //         }
            
    //         // Save logic here
    //         productRuleService.saveProductRule(productRuleDTO);
            
    //         response.put("success", true);
    //         response.put("message", "Product rule saved successfully");
    //         return ResponseEntity.ok(response);
    //     } catch (Exception e) {
    //         // log.error("Error saving product rule", e);
    //         response.put("success", false);
    //         response.put("message", "Failed to save product rule: " + e.getMessage());
    //         return ResponseEntity.badRequest().body(response);
    //     }
    // }
    @PostMapping("/product-rules/save")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveProductRule(@RequestBody ProductRuleDTO productRuleDTO) {
        try {
            productRuleService.saveProductRule(productRuleDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Product rule saved successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/product-rules/get/{id}")
    @ResponseBody
    public Map<String, Object> getProductRule(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            ProductRuleDTO rule = productRuleService.findProductRuleById(id)
                .orElseThrow(() -> new RuntimeException("Product rule not found"));
            response.put("success", true);
            response.put("data", rule);
            return response;
        } catch (Exception e) {
            //log.error("Error fetching product rule with id: {}", id, e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }

    //========  C O M P O N E N T =========================== =======
    @GetMapping("/rule-component/datatables")
    @ResponseBody
    public Map<String, Object> getDataTables(
            @RequestParam int draw,
            @RequestParam int start,
            @RequestParam int length,
            @RequestParam int orderColumn,
            @RequestParam String orderDir,           
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long productRuleId
        ) {
        
        int page = start / length;
        String sortBy = getSortColumnC(orderColumn);
        System.out.println("produk rule="+productRuleId);
        Page<ProductRuleComponentDTO> componentPage = service.getComponentsWithFilter(
            page, length, sortBy, orderDir, productRuleId, isActive, search
        );
        
        Map<String, Object> result = new HashMap<>();
        result.put("draw", draw);
        result.put("recordsTotal", componentPage.getTotalElements());
        result.put("recordsFiltered", componentPage.getTotalElements());
        result.put("data", componentPage.getContent());
        
        return result;
    }
    
    // @GetMapping("/rule-component/get/{id}")
    // public ResponseEntity<ProductRuleComponentDTO> getById(@PathVariable Long id) {
    //     return ResponseEntity.ok(service.getComponentById(id));
    // }

    @GetMapping("/rule-component/get/{id}")
    @ResponseBody
    public Map<String, Object> getByIdComponent(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            ProductRuleComponentProjection2 rule = service.getComponentById(id);
            response.put("success", true);
            response.put("data", rule);
            return response;
        } catch (Exception e) {
            //log.error("Error fetching product rule with id: {}", id, e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }
    
    @PostMapping("/rule-component")
    public ResponseEntity<ProductRuleComponentDTO> createComponent(@RequestBody ProductRuleComponentDTO dto) {
        return ResponseEntity.ok(service.createComponent(dto));
    }
    
    @PostMapping("/rule-component/update/{id}")
    public ResponseEntity<ProductRuleComponentDTO> updateComponent(
            @PathVariable Long id, 
            @RequestBody ProductRuleComponentDTO dto) {
        return ResponseEntity.ok(service.updateComponent(id, dto));
    }
    
    @DeleteMapping("/rule-component/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteComponent(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/rule-component/{id}/toggle")
    public ResponseEntity<ProductRuleComponentDTO> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(service.toggleActive(id));
    }
    
    private String getSortColumnC(int orderColumn) {
        switch (orderColumn) {
            case 0: return "id";
            case 1: return "productRuleId";
            case 2: return "componentCode";
            case 3: return "componentName";
            case 4: return "calculationType";
            case 5: return "amountSource";
            case 6: return "cashMovementType";
            case 7: return "isActive";
            case 8: return "createdBy";
            case 9: return "createdAt";
            case 10: return "updatedBy";
            case 11: return "updatedAt";
            default: return "id";
        }
    }
    
    
    
    //= = = = = = = = = = =  J O U R N A L = = = = = = = = = = = = = = = =
    
    // @GetMapping("/rule-journal/datatables")
    // @ResponseBody
    // public Map<String, Object> getDataTables(
    //         @RequestParam int draw,
    //         @RequestParam int start,
    //         @RequestParam int length,
    //         @RequestParam int orderColumn,
    //         @RequestParam String orderDir,
    //         @RequestParam(required = false) Long productRuleId,
    //         @RequestParam(required = false) Long journalId,
    //         @RequestParam(required = false) Boolean isActive,
    //         @RequestParam(required = false) String search) {
        
    //     int page = start / length;
    //     String sortBy = getSortColumn(orderColumn);
        
    //     Page<ProductRuleJournalDTO> journalPage = serviceRuleJournal.getJournalsWithFilter(
    //         page, length, sortBy, orderDir, productRuleId, journalId, isActive, search
    //     );
        
    //     Map<String, Object> result = new HashMap<>();
    //     result.put("draw", draw);
    //     result.put("recordsTotal", journalPage.getTotalElements());
    //     result.put("recordsFiltered", journalPage.getTotalElements());
    //     result.put("data", journalPage.getContent());
        
    //     return result;
    // }
  
    // @GetMapping("/rule-journal/get/{id}")
    // @ResponseBody
    // public Map<String, Object> getByIdRuleJournal(@PathVariable Long id) {
    //     Map<String, Object> response = new HashMap<>();
    //     try {
    //         ProductRuleJournalDTO rule = serviceRuleJournal.getJournalById(id);
    //             // .orElseThrow(() -> new RuntimeException("Product rule not found"));
    //         response.put("success", true);
    //         response.put("data", rule);
    //         return response;
    //     } catch (Exception e) {
    //         //log.error("Error fetching product rule with id: {}", id, e);
    //         response.put("success", false);
    //         response.put("message", e.getMessage());
    //         return response;
    //     }
    // }
   

    
    // @PostMapping("/rule-journal")
    // public ResponseEntity<ProductRuleJournalDTO> createRuleJournal(@RequestBody ProductRuleJournalDTO dto) {
    //     return ResponseEntity.ok(serviceRuleJournal.createJournal(dto));
    // }
    
    // @PostMapping("/rule-journal/update/{id}")
    // public ResponseEntity<ProductRuleJournalDTO> updateRuleJournal(
    //         @PathVariable Long id, 
    //         @RequestBody ProductRuleJournalDTO dto) {
    //     return ResponseEntity.ok(serviceRuleJournal.updateJournal(id, dto));
    // }
    
    // @DeleteMapping("/rule-journal/{id}")
    // public ResponseEntity<Void> deleteRuleJournal(@PathVariable Long id) {
    //     serviceRuleJournal.deleteJournal(id);
    //     return ResponseEntity.noContent().build();
    // }
    
    // @PatchMapping("/rule-journal/{id}/toggle")
    // public ResponseEntity<ProductRuleJournalDTO> toggleActiveRuleJournal(@PathVariable Long id) {
    //     return ResponseEntity.ok(serviceRuleJournal.toggleActive(id));
    // }
    
    // @GetMapping("/rule-journal/{productRuleId}")
    // public ResponseEntity<Map<String, Object>> getByProductRule(
    //         @PathVariable Long productRuleId,
    //         @RequestParam int draw,
    //         @RequestParam int start,
    //         @RequestParam int length,
    //         @RequestParam int orderColumn,
    //         @RequestParam String orderDir) {
        
    //     int page = start / length;
    //     String sortBy = getSortColumnJ(orderColumn);
        
    //     Page<ProductRuleJournalDTO> journalPage = serviceRuleJournal.getJournalsByProductRule(
    //         productRuleId, page, length, sortBy, orderDir
    //     );
        
    //     Map<String, Object> result = new HashMap<>();
    //     result.put("draw", draw);
    //     result.put("recordsTotal", journalPage.getTotalElements());
    //     result.put("recordsFiltered", journalPage.getTotalElements());
    //     result.put("data", journalPage.getContent());
        
    //     return ResponseEntity.ok(result);
    // }
    
    private String getSortColumnJ(int orderColumn) {
        switch (orderColumn) {
            case 0: return "id";
            case 1: return "productRuleId";
            case 2: return "journalId";
            case 3: return "sequenceNo";
            case 4: return "isActive";
            case 5: return "createdBy";
            case 6: return "createdAt";
            case 7: return "updatedBy";
            case 8: return "updatedAt";
            default: return "id";
        }
    }

   
    
}
