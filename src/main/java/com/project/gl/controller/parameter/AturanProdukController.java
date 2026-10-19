package com.project.gl.controller.parameter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.gl.controller.BaseController;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.Product;
import com.project.gl.model.request.ProductRequest;
import com.project.gl.model.response.PageResponse;
import com.project.gl.model.response.ProductDataTableResponse;
import com.project.gl.model.response.ProductResponse;
import com.project.gl.service.ProductService;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/parameter/aturan-produk") 
public class AturanProdukController extends BaseController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("breadcrumbItems", new String[]{"PARAMETER", "ATURAN PRODUK" });
        setActiveMenu(model, "aturan-produk", "parameter");        

        return "parameter/aturanProduk/index";
    }

    @GetMapping("/datatables")
    @ResponseBody
    public Map<String, Object> getDataTables(
            @RequestParam int draw,
            @RequestParam int start,
            @RequestParam int length,
            @RequestParam int orderColumn,
            @RequestParam String orderDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active) {
        
       // Calculate page number
        int page = start / length;
        
        // Get sort column name
        String sortBy = getSortColumn(orderColumn);
        
        // Get paginated data
        Page<ProductDataTableResponse> productPage = productService.getProductsForDataTables(
                search, active, page, length, sortBy, orderDir);
        
        // Build response
        Map<String, Object> result = new HashMap<>();
        result.put("draw", draw);
        result.put("recordsTotal", productPage.getTotalElements());
        result.put("recordsFiltered", productPage.getTotalElements());
        result.put("data", productPage.getContent());
        
        return result;
    }

    // Helper method to map order column to field name
    private String getSortColumn(int orderColumn) {
        return switch (orderColumn) {
            case 0 -> "id";
            case 1 -> "productCode";
            case 2 -> "productName";
            case 3 -> "isActive";
            case 4 -> "createdAt";
            case 5 -> "updatedAt";
            default -> "id";
        };
    }


    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /*
      // ==================== PRODUCT ENDPOINTS ====================

   

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        log.info("GET /api/v1/products/{} - Get product by id", id);
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{productCode}")
    public ResponseEntity<ProductResponse> getProductByCode(@PathVariable String productCode) {
        log.info("GET /api/v1/products/code/{} - Get product by code", productCode);
        ProductResponse response = productService.getProductByCode(productCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active) {
        log.info("GET /api/v1/products - Get all products with search: {}, active: {}", search, active);
        
        List<ProductResponse> products;
        if (search != null && !search.isEmpty()) {
            products = productService.searchProductsByName(search);
        } else if (active != null && active) {
            products = productService.getActiveProducts();
        } else {
            products = productService.getAllProducts();
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ProductResponse>> getActiveProducts() {
        log.info("GET /api/v1/products/active - Get active products");
        List<ProductResponse> products = productService.getActiveProducts();
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        log.info("PUT /api/v1/products/{} - Update product", id);
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateProduct(@PathVariable Long id) {
        log.info("PATCH /api/v1/products/{}/deactivate - Deactivate product", id);
        productService.deactivateProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateProduct(@PathVariable Long id) {
        log.info("PATCH /api/v1/products/{}/activate - Activate product", id);
        productService.activateProduct(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("DELETE /api/v1/products/{} - Delete product", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/{productCode}")
    public ResponseEntity<Boolean> checkProductCodeExists(@PathVariable String productCode) {
        log.info("GET /api/v1/products/exists/{} - Check product code exists", productCode);
        boolean exists = productService.isProductCodeExists(productCode);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/{id}/with-rules")
    public ResponseEntity<ProductWithRulesResponse> getProductWithActiveRules(@PathVariable Long id) {
        log.info("GET /api/v1/products/{}/with-rules - Get product with active rules", id);
        ProductWithRulesResponse response = productService.getProductWithActiveRules(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/with-rules-by-date")
    public ResponseEntity<ProductWithRulesResponse> getProductWithRulesByDate(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("GET /api/v1/products/{}/with-rules-by-date?date={} - Get product with rules by date", id, date);
        ProductWithRulesResponse response = productService.getProductWithRulesByDate(id, date);
        return ResponseEntity.ok(response);
    }

    // ==================== PRODUCT RULE ENDPOINTS ====================

    @PostMapping("/rules")
    public ResponseEntity<ProductRuleResponse> addProductRule(@Valid @RequestBody ProductRuleRequest request) {
        log.info("POST /api/v1/products/rules - Add product rule");
        ProductRuleResponse response = productService.addProductRule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/rules/{ruleId}")
    public ResponseEntity<ProductRuleResponse> getProductRuleById(@PathVariable Long ruleId) {
        log.info("GET /api/v1/products/rules/{} - Get product rule by id", ruleId);
        ProductRuleResponse response = productService.getProductRuleById(ruleId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}/rules")
    public ResponseEntity<List<ProductRuleResponse>> getProductRulesByProductId(@PathVariable Long productId) {
        log.info("GET /api/v1/products/{}/rules - Get rules by product id", productId);
        List<ProductRuleResponse> responses = productService.getProductRulesByProductId(productId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{productId}/rules/active")
    public ResponseEntity<List<ProductRuleResponse>> getActiveProductRulesByProductId(@PathVariable Long productId) {
        log.info("GET /api/v1/products/{}/rules/active - Get active rules by product id", productId);
        List<ProductRuleResponse> responses = productService.getActiveProductRulesByProductId(productId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/code/{productCode}/rules")
    public ResponseEntity<List<ProductRuleResponse>> getProductRulesByProductCode(@PathVariable String productCode) {
        log.info("GET /api/v1/products/code/{}/rules - Get rules by product code", productCode);
        List<ProductRuleResponse> responses = productService.getProductRulesByProductCode(productCode);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{productId}/rules/by-tenor")
    public ResponseEntity<List<ProductRuleResponse>> getProductRulesByTenor(
            @PathVariable Long productId,
            @RequestParam Integer tenor) {
        log.info("GET /api/v1/products/{}/rules/by-tenor?tenor={} - Get rules by tenor", productId, tenor);
        List<ProductRuleResponse> responses = productService.getProductRulesByTenor(productId, tenor);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/rules/{ruleId}")
    public ResponseEntity<ProductRuleResponse> updateProductRule(
            @PathVariable Long ruleId,
            @Valid @RequestBody ProductRuleRequest request) {
        log.info("PUT /api/v1/products/rules/{} - Update product rule", ruleId);
        ProductRuleResponse response = productService.updateProductRule(ruleId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/rules/{ruleId}/deactivate")
    public ResponseEntity<Void> deactivateProductRule(@PathVariable Long ruleId) {
        log.info("PATCH /api/v1/products/rules/{}/deactivate - Deactivate product rule", ruleId);
        productService.deactivateProductRule(ruleId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/rules/{ruleId}/activate")
    public ResponseEntity<Void> activateProductRule(@PathVariable Long ruleId) {
        log.info("PATCH /api/v1/products/rules/{}/activate - Activate product rule", ruleId);
        productService.activateProductRule(ruleId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/rules/{ruleId}")
    public ResponseEntity<Void> deleteProductRule(@PathVariable Long ruleId) {
        log.info("DELETE /api/v1/products/rules/{} - Delete product rule", ruleId);
        productService.deleteProductRule(ruleId);
        return ResponseEntity.noContent().build();
    }
    
    */
}
