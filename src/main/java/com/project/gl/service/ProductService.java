package com.project.gl.service;

import com.project.gl.model.request.ProductRequest;
import com.project.gl.model.request.ProductRuleRequest;
import com.project.gl.model.response.ProductDataTableResponse;
import com.project.gl.model.response.ProductResponse;
import com.project.gl.model.response.ProductRuleDTO;
import com.project.gl.model.response.ProductWithRulesResponse;
import com.project.gl.model.entity.Product;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;

public interface ProductService {
    
    ProductResponse createProduct(ProductRequest request);

    List<Product> getAll();
    
    // ProductResponse updateProduct(Long id, ProductRequest request);
    
    // ProductResponse getProductById(Long id);
    
    // ProductResponse getProductByCode(String productCode);
    
    // List<ProductResponse> getAllProducts();
    
    // List<ProductResponse> getActiveProducts();
    
    // List<ProductResponse> searchProductsByName(String productName);
    
    // void deleteProduct(Long id);
    
    // void deactivateProduct(Long id);
    
    // void activateProduct(Long id);
    
    boolean isProductCodeExists(String productCode);
    
   
    
    // // Product Rule methods
    // ProductRuleResponse addProductRule(ProductRuleRequest request);
    
    // ProductRuleResponse updateProductRule(Long ruleId, ProductRuleRequest request);
    
    // ProductRuleResponse getProductRuleById(Long ruleId);
    
    // List<ProductRuleResponse> getProductRulesByProductId(Long productId);
    
    // List<ProductRuleResponse> getActiveProductRulesByProductId(Long productId);
    
    // List<ProductRuleResponse> getProductRulesByProductCode(String productCode);
    
    // void deleteProductRule(Long ruleId);
    
    // void deactivateProductRule(Long ruleId);
    
    // void activateProductRule(Long ruleId);
    
    // ProductWithRulesResponse getProductWithActiveRules(Long productId);
    
    // ProductWithRulesResponse getProductWithRulesByDate(Long productId, LocalDate date);
    
    // List<ProductRuleResponse> getProductRulesByTenor(Long productId, Integer tenor);

    Product getProductEntityById(Long id);
    Page<ProductDataTableResponse> getProductsForDataTables(
            String search, 
            Boolean active, 
            int page, 
            int size, 
            String sortBy, 
            String sortDirection);
    
    // long countProductsForDataTables(String search, Boolean active);
}