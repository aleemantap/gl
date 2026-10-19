package com.project.gl.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.apache.poi.hpsf.Decimal;

public class ProductRuleComponentDTO {
    private Long id;
    private Long productRuleId;
    private Long componentId;
    private String componentCode;
    private String componentName;
    private String calculationType;
    private String amountSource;
    private BigDecimal fixedAmount;
    // private String cashMovementType;
    private Boolean isActive;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

     public Long getComponentId() {
        return componentId;
    }
    public void setComponentId(Long componentId) {
        this.componentId = componentId;
    }

    public Long getProductRuleId() {
        return productRuleId;
    }
    public void setProductRuleId(Long productRuleId) {
        this.productRuleId = productRuleId;
    }
    public String getComponentCode() {
        return componentCode;
    }
    public void setComponentCode(String componentCode) {
        this.componentCode = componentCode;
    }
    public String getComponentName() {
        return componentName;
    }
    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }
    public String getCalculationType() {
        return calculationType;
    }
    public void setCalculationType(String calculationType) {
        this.calculationType = calculationType;
    }
    public String getAmountSource() {
        return amountSource;
    }
    public void setAmountSource(String amountSource) {
        this.amountSource = amountSource;
    }


    public BigDecimal getFixedAmount() {
        return fixedAmount;
    }
    public void setFixedAmount(BigDecimal fixedAmount) {
        this.fixedAmount = fixedAmount;
    }


    

    // public String getCashMovementType() {
    //     return cashMovementType;
    // }
    // public void setCashMovementType(String cashMovementType) {
    //     this.cashMovementType = cashMovementType;
    // }
    public Boolean getIsActive() {
        return isActive;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    public String getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public String getUpdatedBy() {
        return updatedBy;
    }
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
