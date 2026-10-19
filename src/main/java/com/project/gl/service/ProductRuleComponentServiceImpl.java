package com.project.gl.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.gl.model.response.ProductRuleComponentDTO;
import com.project.gl.model.entity.Component;
import com.project.gl.model.entity.ProductRuleComponent;
import com.project.gl.model.projection.ProductRuleComponentProjection;
import com.project.gl.model.projection.ProductRuleComponentProjection2;
import com.project.gl.repo.ProductRuleComponentRepository;
import com.project.gl.service.ProductRuleComponentService;
import com.project.gl.repo.ComponentRepository;


@Service
@Transactional
public class ProductRuleComponentServiceImpl implements ProductRuleComponentService {
    
    @Autowired
    private ProductRuleComponentRepository repository;
    
    @Autowired
    private ComponentRepository componentRepository;
    
    

     @Override
     public Page<ProductRuleComponentDTO> getComponentsWithFilter(
            int page, int size, String sortBy, String orderDir,
            Long productRuleId, Boolean isActive, String search) {
        
        String validSortBy = validateSortColumn(sortBy);
        
        Sort sort = orderDir.equalsIgnoreCase("asc") 
            ? Sort.by(validSortBy).ascending() 
            : Sort.by(validSortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Ambil projection dari repository
        Page<ProductRuleComponentProjection> projectionPage = repository.findWithFilters(
            productRuleId, isActive, search, pageable
        );
        
        // Convert ke DTO
        return projectionPage.map(this::convertProjectionToDTO);
    }

      private ProductRuleComponentDTO convertProjectionToDTO(ProductRuleComponentProjection projection) {
        ProductRuleComponentDTO dto = new ProductRuleComponentDTO();
        dto.setId(projection.getId());
        dto.setProductRuleId(projection.getProductRuleId());
        dto.setComponentCode(projection.getComponentCode());
        dto.setComponentName(projection.getComponentName());
        dto.setCalculationType(projection.getCalculationType());
        dto.setAmountSource(projection.getAmountSource());
        dto.setFixedAmount(projection.getFixedAmount());
        // dto.setCashMovementType(projection.getCashMovementType());
        dto.setIsActive(projection.getActive());
        dto.setCreatedBy(projection.getCreatedBy());
        dto.setCreatedAt(projection.getCreatedAt());
        dto.setUpdatedBy(projection.getUpdatedBy());
        dto.setUpdatedAt(projection.getUpdatedAt());
        return dto;
    }
    

    @Override
    public ProductRuleComponentProjection2 getComponentById(Long id) {
        return repository.findProjectionById(id)
            .orElseThrow(() -> new RuntimeException("Component not found with id: " + id));
    }

  
    @Override
    @Transactional
    public ProductRuleComponentDTO createComponent(ProductRuleComponentDTO dto) {
        // Validate unique constraint
        validateUniqueComponent(dto.getProductRuleId(), dto.getComponentId(), null);
        
        ProductRuleComponent component = convertToEntity(dto);
        // component.setActive(true);
        // component.setCreatedAt(LocalDateTime.now());
        // component.setCreatedBy(getCurrentUser());        
        ProductRuleComponent saved = repository.save(component);
        return convertToDTO(saved);
    }
    
    @Override
    @Transactional
    public ProductRuleComponentDTO updateComponent(Long id, ProductRuleComponentDTO dto) {
        ProductRuleComponent existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Component not found with id: " + id));
        
        // Validate unique constraint (excluding current id)
        validateUniqueComponent(dto.getProductRuleId(), dto.getComponentId(), id);
        
        // Update fields
        existing.setProductRuleId(dto.getProductRuleId());
        // existing.setComponentCode(dto.getComponentCode());
        // existing.setComponentName(dto.getComponentName());
        existing.setCalculationType(dto.getCalculationType());
        existing.setAmountSource(dto.getAmountSource());
        // existing.setCashMovementType(dto.getCashMovementType());
        existing.setActive(dto.getIsActive());
        // existing.setUpdatedBy(getCurrentUser());
        // existing.setUpdatedAt(LocalDateTime.now());
        
        ProductRuleComponent updated = repository.save(existing);
        return convertToDTO(updated);
    }
    
    @Override
    @Transactional
    public void deleteComponent(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Component not found with id: " + id);
        }
        repository.deleteById(id);
    }
    
    @Override
    @Transactional
    public ProductRuleComponentDTO toggleActive(Long id) {
        ProductRuleComponent component = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Component not found with id: " + id));
        
        component.setActive(!component.getActive());
        // component.setUpdatedBy(getCurrentUser());
        // component.setUpdatedAt(LocalDateTime.now());
        
        ProductRuleComponent updated = repository.save(component);
        return convertToDTO(updated);
    }
    
    // ==================== PRIVATE METHODS ====================
    
    /**
     * Convert Entity to DTO
     */
    private ProductRuleComponentDTO convertToDTO(ProductRuleComponent component) {
        ProductRuleComponentDTO dto = new ProductRuleComponentDTO();
        dto.setId(component.getId());
        dto.setProductRuleId(component.getProductRuleId());
        // dto.setComponentId(component.getComponent().getId());
        dto.setComponentId(component.getComponentId());
        
        // dto.setComponentName(component.getComponentName());
        dto.setCalculationType(component.getCalculationType());
        dto.setAmountSource(component.getAmountSource());
        // dto.setCashMovementType(component.getCashMovementType());
        dto.setIsActive(component.getActive());
        dto.setCreatedBy(component.getCreatedBy());
        dto.setCreatedAt(component.getCreatedAt());
        dto.setUpdatedBy(component.getUpdatedBy());
        dto.setUpdatedAt(component.getUpdatedAt());
        return dto;
    }
    
    /**
     * Convert DTO to Entity
     */
    private ProductRuleComponent convertToEntity(ProductRuleComponentDTO dto) {
        ProductRuleComponent component = new ProductRuleComponent();
        component.setId(dto.getId());
        component.setProductRuleId(dto.getProductRuleId());
        component.setComponentId(dto.getComponentId());
        // component.setComponentCode(dto.getComponentCode());
        // component.setComponentName(dto.getComponentName());

        // if (dto.getComponentId() != null) {

        //     Component componentx = componentRepository.findById(dto.getComponentId())
        //             .orElseThrow(() -> new RuntimeException(
        //                     "Component dengan ID " + dto.getComponentId() + " tidak ditemukan"
        //             ));

        //     component.setComponent(componentx);
        // }

        component.setCalculationType(dto.getCalculationType());
        component.setAmountSource(dto.getAmountSource());
        // component.setCashMovementType(dto.getCashMovementType());
        component.setActive(dto.getIsActive());
        return component;
    }
    
    /**
     * Validate unique constraint (product_rule_id + component_code)
     */
    // private void validateUniqueComponent(Long productRuleId, String componentCode, Long excludeId) {
    //     Optional<ProductRuleComponent> existing = repository.findByProductRuleIdAndComponentId(
    //         productRuleId, componentId
    //     );
        
    //     if (existing.isPresent() && !existing.get().getId().equals(excludeId)) {
    //         throw new RuntimeException(
    //             "Component with code '" + componentId + 
    //             "' already exists for product rule ID: " + productRuleId
    //         );
    //     }
    // }
    private void validateUniqueComponent(
        Long productRuleId,
        Long componentId,
        Long excludeId) {

        Optional<ProductRuleComponent> existing =
                repository.findByProductRuleIdAndComponentId(
                        productRuleId,
                        componentId
                );

        if (existing.isPresent()
                && (excludeId == null
                    || !existing.get().getId().equals(excludeId))) {

            throw new RuntimeException(
                    "Component with ID '" + componentId
                    + "' already exists for product rule ID: "
                    + productRuleId
            );
        }
    }
    
    /**
     * Validate sort column to prevent SQL injection
     */
    private String validateSortColumn(String sortBy) {
        // List of allowed column names
        String[] allowedColumns = {
            "id", "productRuleId", "componentCode", "componentName", 
            "calculationType", "amountSource", "cashMovementType", 
            "isActive", "createdBy", "createdAt", "updatedBy", "updatedAt"
        };
        
        for (String allowed : allowedColumns) {
            if (allowed.equals(sortBy)) {
                return sortBy;
            }
        }
        return "id"; // Default sort column
    }
    
    /**
     * Get current logged-in user (from SecurityContext or session)
     */
    private String getCurrentUser() {
        // Implement based on your security framework
        // For Spring Security:
        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // return auth.getName();
        
        // Temporary implementation
        return "system";
    }
}