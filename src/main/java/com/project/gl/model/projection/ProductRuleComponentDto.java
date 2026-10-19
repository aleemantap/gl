package com.project.gl.model.projection;

import java.time.LocalDateTime;

public interface ProductRuleComponentDto {
    Long getId();
    Long getProductRuleId();
    String getComponentCode();
    String getComponentName();
    String getCalculationType();
    String getAmountSource();
    String getCashMovementType();
    Boolean getActive();
    String getCreatedBy();
    LocalDateTime getCreatedAt();
    String getUpdatedBy();
    LocalDateTime getUpdatedAt();
}