package com.project.gl.service;

import org.springframework.data.domain.Page;

import com.project.gl.model.projection.ProductRuleComponentProjection2;
import com.project.gl.model.response.ProductRuleComponentDTO;

public interface ProductRuleComponentService {
    
    /**
     * Get product rule components with filters for DataTables
     */
    Page<ProductRuleComponentDTO> getComponentsWithFilter(
        int page, 
        int size, 
        String sortBy, 
        String orderDir,
        Long productRuleId, 
        Boolean isActive, 
        String search
    );
    
    /**
     * Get component by ID
     */
    ProductRuleComponentProjection2 getComponentById(Long id);
    
    /**
     * Create new component
     */
    ProductRuleComponentDTO createComponent(ProductRuleComponentDTO dto);
    
    /**
     * Update existing component
     */
    ProductRuleComponentDTO updateComponent(Long id, ProductRuleComponentDTO dto);
    
    /**
     * Delete component by ID
     */
    void deleteComponent(Long id);
    
    /**
     * Toggle active status
     */
    ProductRuleComponentDTO toggleActive(Long id);
}