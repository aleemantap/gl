package com.project.gl.service;

import com.project.gl.model.request.ProductRequest;
import com.project.gl.model.request.ProductRuleRequest;
import com.project.gl.model.response.ProductDataTableResponse;
import com.project.gl.model.response.ProductResponse;
import com.project.gl.model.response.ProductRuleDetailResponse;
import com.project.gl.model.response.ProductRuleDTO;
// import com.project.gl.model.response.ProductRuleResponse;

import com.project.gl.model.response.ProductRuleSummary;
import com.project.gl.model.response.ProductWithRulesResponse;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.Product;
import com.project.gl.model.entity.ProductRule;
import com.project.gl.repo.ProductRepository;
import com.project.gl.repo.ProductRuleRepository;
import com.project.gl.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    
    // @Autowired
    // private ProductRuleRepository productRuleRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDataTableResponse> getProductsForDataTables(
            String search, 
            Boolean active, 
            int page, 
            int size, 
            String sortBy, 
            String sortDirection) {
        
        // log.info("Fetching products for DataTables - search: {}, active: {}, page: {}, size: {}, sortBy: {}, sortDir: {}", 
        //         search, active, page, size, sortBy, sortDirection);
        
        // Determine sort direction
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);        
        // Map sort field to entity field
        String sortField = mapSortField(sortBy);        
        // Create pageable
        PageRequest pageable = PageRequest.of(page, size, Sort.by(direction, sortField));        
        // Execute query with filters
        Page<Product> productPage = productRepository.findProductsWithFilter(search, active, pageable);
        
        // Convert to DataTable response
        return productPage.map(this::convertToDataTableResponse);
    }

    private String mapSortField(String sortBy) {
        if (sortBy == null) {
            return "id";
        }
        
        return switch (sortBy) {
            case "productCode" -> "productCode";
            case "productName" -> "productName";
            case "isActive" -> "isActive";
            case "status" -> "isActive";
            case "createdDate" -> "createdAt";
            case "updatedDate" -> "updatedAt";
            default -> "id";
        };
    }

     private ProductDataTableResponse convertToDataTableResponse(Product product) {
        // Get rules summary
        List<ProductRuleSummary> ruleSummaries = product.getRules().stream()
                .filter(ProductRule::getIsActive)
                .map(this::convertToRuleSummary)
                .collect(Collectors.toList());

        return ProductDataTableResponse.builder()
                .id(product.getId())
                .productCode(product.getProductCode())
                .productName(product.getProductName())
                .isActive(product.getIsActive())
                .status(product.getIsActive() ? "Active" : "Inactive")
                .totalRules(ruleSummaries.size())
                .createdDate(product.getCreatedAt() != null ? 
                        product.getCreatedAt().format(DATE_FORMATTER) : null)
                .updatedDate(product.getUpdatedAt() != null ? 
                        product.getUpdatedAt().format(DATE_FORMATTER) : null)
                .rules(ruleSummaries)
                .build();
    }

    private ProductRuleSummary convertToRuleSummary(ProductRule rule) {
        return ProductRuleSummary.builder()
                .id(rule.getId())
                //.interestRate(rule.getInterestRate())
                // .adminFee(rule.getAdminFee())
                // .minTenor(rule.getMinTenor())
                // .maxTenor(rule.getMaxTenor())
                .isActive(rule.getIsActive())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }


    // @Override
    // public ProductResponse createProduct(ProductRequest request) {
    //     if (isProductCodeExists(request.getProductCode())) {
    //         throw new RuntimeException("Product code already exists: " + request.getProductCode());
    //     }
    //     Product product = Product.builder()
    //             .productCode(request.getProductCode())
    //             .productName(request.getProductName())
    //             .isActive(request.getIsActive() != null ? request.getIsActive() : true)
    //             .build();

    //     Product savedProduct = productRepository.save(product);
    //     log.info("Product created successfully with id: {}", savedProduct.getId());
        
    //     return convertToResponse(savedProduct);
    // }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsByProductCode(request.getProductCode())) {
            throw new RuntimeException("Product code already exists: " + request.getProductCode());
        }
        
        Product product = Product.builder()
                .productCode(request.getProductCode())
                .productName(request.getProductName())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
        
        productRepository.save(product);
        
        // Return response sederhana
        return ProductResponse.builder()
                .productCode(request.getProductCode())
                .productName(request.getProductName())
                .isActive(product.getIsActive())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductCodeExists(String productCode) {
        return productRepository.existsByProductCode(productCode);
    }
    @Override
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    // private ProductResponse convertToResponse(Product product) {
    //     List<ProductRuleResponse> ruleResponses = product.getRules().stream()
    //             .map(this::convertRuleToResponse)
    //             .collect(Collectors.toList());

    //     return ProductResponse.builder()
    //             .id(product.getId())
    //             .productCode(product.getProductCode())
    //             .productName(product.getProductName())
    //             .isActive(product.getIsActive())
    //             .rules(ruleResponses)
    //             .build();
    // }

   /* 

    @Override
    @Transactional(readOnly = true)
    public long countProductsForDataTables(String search, Boolean active) {
        return productRepository.countProductsWithFilter(search, active);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.info("Updating product with id: {}", id);
        
        Product product = getProductEntityById(id);
        
        if (!product.getProductCode().equals(request.getProductCode()) 
                && isProductCodeExists(request.getProductCode())) {
            throw new RuntimeException("Product code already exists: " + request.getProductCode());
        }

        product.setProductCode(request.getProductCode());
        product.setProductName(request.getProductName());
        if (request.getIsActive() != null) {
            product.setIsActive(request.getIsActive());
        }

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully with id: {}", updatedProduct.getId());
        
        return convertToResponse(updatedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.info("Fetching product with id: {}", id);
        Product product = getProductEntityById(id);
        return convertToResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductByCode(String productCode) {
        log.info("Fetching product with code: {}", productCode);
        Product product = productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new RuntimeException("Product not found with code: " + productCode));
        return convertToResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getActiveProducts() {
        log.info("Fetching active products");
        return productRepository.findByIsActiveTrue().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProductsByName(String productName) {
        // log.info("Searching products by name: {}", productName);
        return productRepository.findByProductNameContainingIgnoreCase(productName).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);
        Product product = getProductEntityById(id);
        
        // Check if product has active rules
        if (productRuleRepository.existsByProductIdAndIsActiveTrue(id)) {
            throw new RuntimeException("Cannot delete product with active rules. Deactivate rules first.");
        }
        
        productRepository.delete(product);
        log.info("Product deleted successfully with id: {}", id);
    }

    @Override
    public void deactivateProduct(Long id) {
        log.info("Deactivating product with id: {}", id);
        Product product = getProductEntityById(id);
        product.setIsActive(false);
        productRepository.save(product);
        log.info("Product deactivated successfully with id: {}", id);
    }

    @Override
    public void activateProduct(Long id) {
        log.info("Activating product with id: {}", id);
        Product product = getProductEntityById(id);
        product.setIsActive(true);
        productRepository.save(product);
        log.info("Product activated successfully with id: {}", id);
    }

  

   

    // ==================== PRODUCT RULE METHODS ====================

    @Override
    public ProductRuleResponse addProductRule(ProductRuleRequest request) {
        log.info("Adding new rule for product id: {}", request.getProductId());
        
        Product product = getProductEntityById(request.getProductId());
        
        // Validate tenor
        if (request.getMinTenor() > request.getMaxTenor()) {
            throw new RuntimeException("Min tenor cannot be greater than max tenor");
        }
        
        // Validate effective date
        if (request.getEffectiveDate() == null) {
            request.setEffectiveDate(LocalDate.now());
        }

        ProductRule rule = ProductRule.builder()
                .product(product)
                .interestRate(request.getInterestRate())
                .adminFee(request.getAdminFee())
                .provisionRate(request.getProvisionRate())
                .minTenor(request.getMinTenor())
                .maxTenor(request.getMaxTenor())
                .auctionTriggerDays(request.getAuctionTriggerDays())
                .adminInAdvance(request.getAdminInAdvance())
                .provisionInAdvance(request.getProvisionInAdvance())
                .effectiveDate(request.getEffectiveDate())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        ProductRule savedRule = productRuleRepository.save(rule);
        log.info("Product rule added successfully with id: {}", savedRule.getId());
        
        return convertRuleToResponse(savedRule);
    }

    @Override
    public ProductRuleResponse updateProductRule(Long ruleId, ProductRuleRequest request) {
        log.info("Updating product rule with id: {}", ruleId);
        
        ProductRule rule = getProductRuleEntityById(ruleId);
        
        // Validate tenor
        if (request.getMinTenor() > request.getMaxTenor()) {
            throw new RuntimeException("Min tenor cannot be greater than max tenor");
        }
        
        rule.setInterestRate(request.getInterestRate());
        // rule.setAdminFee(request.getAdminFee());
        // rule.setProvisionRate(request.getProvisionRate());
        // rule.setMinTenor(request.getMinTenor());
        // rule.setMaxTenor(request.getMaxTenor());
        rule.setAuctionTriggerDays(request.getAuctionTriggerDays());
        // rule.setAdminInAdvance(request.getAdminInAdvance());
        // rule.setProvisionInAdvance(request.getProvisionInAdvance());
        // rule.setEffectiveDate(request.getEffectiveDate());
        if (request.getIsActive() != null) {
            rule.setIsActive(request.getIsActive());
        }

        ProductRule updatedRule = productRuleRepository.save(rule);
        log.info("Product rule updated successfully with id: {}", updatedRule.getId());
        
        return convertRuleToResponse(updatedRule);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductRuleResponse getProductRuleById(Long ruleId) {
        log.info("Fetching product rule with id: {}", ruleId);
        ProductRule rule = getProductRuleEntityById(ruleId);
        return convertRuleToResponse(rule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductRuleResponse> getProductRulesByProductId(Long productId) {
        log.info("Fetching rules for product id: {}", productId);
        return productRuleRepository.findByProductId(productId).stream()
                .map(this::convertRuleToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductRuleResponse> getActiveProductRulesByProductId(Long productId) {
        log.info("Fetching active rules for product id: {}", productId);
        return productRuleRepository.findByProductIdAndIsActiveTrue(productId).stream()
                .map(this::convertRuleToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductRuleResponse> getProductRulesByProductCode(String productCode) {
        log.info("Fetching rules for product code: {}", productCode);
        return productRuleRepository.findByProduct_ProductCode(productCode).stream()
                .map(this::convertRuleToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProductRule(Long ruleId) {
        log.info("Deleting product rule with id: {}", ruleId);
        ProductRule rule = getProductRuleEntityById(ruleId);
        productRuleRepository.delete(rule);
        log.info("Product rule deleted successfully with id: {}", ruleId);
    }

    @Override
    public void deactivateProductRule(Long ruleId) {
        log.info("Deactivating product rule with id: {}", ruleId);
        ProductRule rule = getProductRuleEntityById(ruleId);
        rule.setIsActive(false);
        productRuleRepository.save(rule);
        log.info("Product rule deactivated successfully with id: {}", ruleId);
    }

    @Override
    public void activateProductRule(Long ruleId) {
        log.info("Activating product rule with id: {}", ruleId);
        ProductRule rule = getProductRuleEntityById(ruleId);
        rule.setIsActive(true);
        productRuleRepository.save(rule);
        log.info("Product rule activated successfully with id: {}", ruleId);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductWithRulesResponse getProductWithActiveRules(Long productId) {
        log.info("Fetching product with active rules for id: {}", productId);
        Product product = getProductEntityById(productId);
        List<ProductRule> activeRules = productRuleRepository.findByProductIdAndIsActiveTrue(productId);
        return convertToProductWithRulesResponse(product, activeRules);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductWithRulesResponse getProductWithRulesByDate(Long productId, LocalDate date) {
        log.info("Fetching product with rules by date: {} for product id: {}", date, productId);
        Product product = getProductEntityById(productId);
        List<ProductRule> rules = productRuleRepository.findActiveRulesByProductAndDate(productId, date);
        return convertToProductWithRulesResponse(product, rules);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductRuleResponse> getProductRulesByTenor(Long productId, Integer tenor) {
        log.info("Fetching active rules for product id: {} with tenor: {}", productId, tenor);
        return productRuleRepository.findActiveRulesByProductAndTenor(productId, tenor).stream()
                .map(this::convertRuleToResponse)
                .collect(Collectors.toList());
    }

    // ==================== HELPER METHODS ====================

    private ProductRule getProductRuleEntityById(Long id) {
        return productRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product rule not found with id: " + id));
    }

  

    // private ProductRuleResponse convertRuleToResponse(ProductRule rule) {
    //     return ProductRuleResponse.builder()
    //             .id(rule.getId())
    //             .productId(rule.getProduct().getId())
    //             .productCode(rule.getProduct().getProductCode())
    //             .productName(rule.getProduct().getProductName())
    //             .interestRate(rule.getInterestRate())
    //             .adminFee(rule.getAdminFee())
    //             .provisionRate(rule.getProvisionRate())
    //             .minTenor(rule.getMinTenor())
    //             .maxTenor(rule.getMaxTenor())
    //             .auctionTriggerDays(rule.getAuctionTriggerDays())
    //             .adminInAdvance(rule.getAdminInAdvance())
    //             .provisionInAdvance(rule.getProvisionInAdvance())
    //             .effectiveDate(rule.getEffectiveDate())
    //             .isActive(rule.getIsActive())
    //             .build();
    // }

    private ProductWithRulesResponse convertToProductWithRulesResponse(Product product, List<ProductRule> rules) {
        List<ProductRuleDetailResponse> ruleDetails = rules.stream()
                .map(this::convertToRuleDetailResponse)
                .collect(Collectors.toList());

        return ProductWithRulesResponse.builder()
                .id(product.getId())
                .productCode(product.getProductCode())
                .productName(product.getProductName())
                .isActive(product.getIsActive())
                .rules(ruleDetails)
                .build();
    }

    private ProductRuleDetailResponse convertToRuleDetailResponse(ProductRule rule) {
        return ProductRuleDetailResponse.builder()
                .id(rule.getId())
                .interestRate(rule.getInterestRate())
                .adminFee(rule.getAdminFee())
                .provisionRate(rule.getProvisionRate())
                .minTenor(rule.getMinTenor())
                .maxTenor(rule.getMaxTenor())
                .auctionTriggerDays(rule.getAuctionTriggerDays())
                .adminInAdvance(rule.getAdminInAdvance())
                .provisionInAdvance(rule.getProvisionInAdvance())
                .effectiveDate(rule.getEffectiveDate())
                .isActive(rule.getIsActive())
                .build();
    }*/
}