package com.project.gl.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
// import com.project.gl.model.response.ProductDataTableResponse;

import com.project.gl.model.response.ProductRuleDTO;

public interface ProductRuleService {
    List<ProductRuleDTO> findAllProductRules();
    Page<ProductRuleDTO> getProductRulesWithFilter(
            int page, int size, String sortBy, String orderDir,
            Long productId,  Boolean isActive, String search);
    Optional<ProductRuleDTO> findProductRuleById(Long id);
    long countProductRules();

    void saveProductRule(ProductRuleDTO productRuleDTO);
}
