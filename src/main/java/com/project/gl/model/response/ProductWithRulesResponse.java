package com.project.gl.model.response;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductWithRulesResponse {
    private Long id;
    private String productCode;
    private String productName;
    private Boolean isActive;
    private List<ProductRuleDetailResponse> rules;
}