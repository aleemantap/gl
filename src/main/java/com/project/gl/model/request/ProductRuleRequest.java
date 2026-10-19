package com.project.gl.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRuleRequest {
    private Long productId;
    private BigDecimal interestRate;
    private BigDecimal adminFee;
    private BigDecimal provisionRate;
    private Integer minTenor;
    private Integer maxTenor;
    private Integer auctionTriggerDays;
    private Boolean adminInAdvance;
    private Boolean provisionInAdvance;
    private LocalDate effectiveDate;
    private Boolean isActive;
}