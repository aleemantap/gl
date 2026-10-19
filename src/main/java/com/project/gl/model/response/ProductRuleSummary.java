package com.project.gl.model.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRuleSummary {
    private Long id;
    private BigDecimal interestRate;
    private BigDecimal adminFee;
    private Integer minTenor;
    private Integer maxTenor;
    private Boolean isActive;
}