package com.project.gl.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDataTableResponse {
    private Long id;
    private String productCode;
    private String productName;
    private Boolean isActive;
    private String status;
    private Integer totalRules;
    private String createdDate;
    private String updatedDate;
    private List<ProductRuleSummary> rules;
}


// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// public class DataTablesResponse<T> {
//     private int draw;
//     private long recordsTotal;
//     private long recordsFiltered;
//     private List<T> data;
// }