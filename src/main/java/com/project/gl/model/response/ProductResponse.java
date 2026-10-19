package com.project.gl.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
// import com.project.gl.model.response.ProductRuleResponse;
// import groovy.transform.builder.Builder;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String productCode;
    private String productName;
    private Boolean isActive;
    // private List<ProductRuleResponse> rules;
}
