package com.project.gl.service;



import com.project.gl.model.response.ProductRuleDTO;
import com.project.gl.model.entity.Product;
// import com.project.gl.model.response.ProductRuleDTO;
import com.project.gl.model.entity.ProductRule;
import com.project.gl.repo.ProductRuleRepository;
import com.project.gl.repo.ProductRepository;
import com.project.gl.service.ProductRuleService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductRuleServiceImpl implements ProductRuleService {
    
    @Autowired
    private  ProductRuleRepository productRuleRepository;

    @Autowired
    private  ProductRepository productRepository;
    
    @Override
    public List<ProductRuleDTO> findAllProductRules() {
        return productRuleRepository.findAllByOrderByIdDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<ProductRuleDTO> getProductRulesWithFilter(
            int page, int size, String sortBy, String orderDir,
            Long productId, Boolean isActive, String search) {
        
        Sort.Direction direction = Sort.Direction.fromString(orderDir);
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ProductRule> productRulePage;
        
        if (search != null && !search.isEmpty()) {
            productRulePage = productRuleRepository.findByFiltersWithSearch(productId, search, pageable);
        } else {
            productRulePage = productRuleRepository.findByFilters(productId, isActive, pageable);
        }
        
        return productRulePage.map(this::convertToDTO);
    }
    
    @Override
    public Optional<ProductRuleDTO> findProductRuleById(Long id) {
        return productRuleRepository.findById(id)
                .map(this::convertToDTO);
    }
    
    @Override
    public long countProductRules() {
        return productRuleRepository.count();
    }
    
    private ProductRuleDTO convertToDTO(ProductRule rule) {
        return ProductRuleDTO.builder()
                .id(rule.getId())
                // .productId(rule.getProduct().getId())
                // .productName("Product " + rule.getProduct().getProductName()) // You can fetch actual product name if needed
                .ruleVersion(rule.getRuleVersion())
                .effectiveFrom(rule.getEffectiveFrom())
                .effectiveTo(rule.getEffectiveTo())
                // .interestRate(rule.getInterestRate())
                // .interestPeriodDays(rule.getInterestPeriodDays())
                // .interestMethod(rule.getInterestMethod())
                // .adminType(rule.getAdminType())
                // .adminValue(rule.getAdminValue())
                // .provisionType(rule.getProvisionType())
                // .provisionValue(rule.getProvisionValue())
                .tenorMinDays(rule.getTenorMinDays())
                .tenorMaxDays(rule.getTenorMaxDays())
                .auctionTriggerDays(rule.getAuctionTriggerDays())
                .isActive(rule.getIsActive())
                .approvalStatus(rule.getApprovalStatus())
                .approvedBy(rule.getApprovedBy())
                .approvedAt(rule.getApprovedAt())
                .sourceSystem(rule.getSourceSystem())
                .createdBy(rule.getCreatedBy())
                .createdAt(rule.getCreatedAt())
                .updatedBy(rule.getUpdatedBy())
                .updatedAt(rule.getUpdatedAt())
                .build();
    }

    // @Override
    // @Transactional
    // public ProductRuleDTO saveProductRule(ProductRuleDTO productRuleDTO) {

    //     Product product = productRepository.findById(productRuleDTO.getProductId())
    //             .orElseThrow(() ->
    //                 new RuntimeException(
    //                     "Product not found with id: "
    //                     + productRuleDTO.getProductId()
    //                 )
    //             );

    //     ProductRule rule = ProductRule.builder()
    //             .product(product)
    //             .ruleVersion(
    //                 productRuleDTO.getRuleVersion() != null
    //                     ? productRuleDTO.getRuleVersion()
    //                     : 1
    //             )
    //             .effectiveFrom(productRuleDTO.getEffectiveFrom())
    //             .effectiveTo(productRuleDTO.getEffectiveTo())
    //             // .interestRate(productRuleDTO.getInterestRate())
    //             // .interestPeriodDays(productRuleDTO.getInterestPeriodDays())
    //             // .interestMethod(productRuleDTO.getInterestMethod())
    //             // .adminType(productRuleDTO.getAdminType())
    //             // .adminValue(productRuleDTO.getAdminValue())
    //             // .provisionType(productRuleDTO.getProvisionType())
    //             // .provisionValue(productRuleDTO.getProvisionValue())
    //             .tenorMinDays(productRuleDTO.getTenorMinDays())
    //             .tenorMaxDays(productRuleDTO.getTenorMaxDays())
    //             .auctionTriggerDays(productRuleDTO.getAuctionTriggerDays())
    //             .isActive(true)
    //             .approvalStatus("PENDING")
    //             .sourceSystem("GADAI")
    //             .build();

    //     ProductRule savedRule = productRuleRepository.save(rule);

    //     return convertToDTO(savedRule);
    // }

    @Override
    @Transactional
    public void saveProductRule(ProductRuleDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        productRuleRepository.save(ProductRule.builder()
                // .product(product)
                .productId(dto.getProductId())  
                .ruleVersion(dto.getRuleVersion() != null ? dto.getRuleVersion() : 1)
                .effectiveFrom(dto.getEffectiveFrom())
                .effectiveTo(dto.getEffectiveTo())
                .tenorMinDays(dto.getTenorMinDays())
                .tenorMaxDays(dto.getTenorMaxDays())
                .auctionTriggerDays(dto.getAuctionTriggerDays())
                .isActive(true)
                .approvalStatus("APPROVED")
                .sourceSystem("GL")
                .build());
    }
}