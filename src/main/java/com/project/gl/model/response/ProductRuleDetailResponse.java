package com.project.gl.model.response;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRuleDetailResponse {
    private Long id;
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