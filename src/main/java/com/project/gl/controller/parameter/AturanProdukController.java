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
        // ProductResponse response = productService.createProduct(request);
        ProductResponse response = productService.createProduct2(request);        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
