package com.project.gl.model.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
// import org.apache.poi.hpsf.Decimal;

public interface ProductRuleComponentProjection {
    Long getId();
    Long getProductRuleId();
    String getComponentCode();
    String getComponentName();
    String getCalculationType();
    String getAmountSource();
    BigDecimal getFixedAmount();
    // String getCashMovementType();
    Boolean getActive();
    String getCreatedBy();
    LocalDateTime getCreatedAt();
    String getUpdatedBy();
    LocalDateTime getUpdatedAt();
}
