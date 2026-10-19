package com.project.gl.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRuleDTO {
    private Long id;
    private Long productId;
    private String productCode;
    private String productName;
    private Integer ruleVersion;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private BigDecimal interestRate;
    private Integer interestPeriodDays;
    private String interestMethod;
    private String adminType;
    private BigDecimal adminValue;
    private String provisionType;
    private BigDecimal provisionValue;
    private Integer tenorMinDays;
    private Integer tenorMaxDays;
    private Integer auctionTriggerDays;
    private Boolean isActive;
    private String approvalStatus;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private String sourceSystem;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    
    // Helper methods for display
    public String getStatusBadge() {
        if (isActive != null && isActive) {
            return "<span class='badge bg-success'>Active</span>";
        } else {
            return "<span class='badge bg-danger'>Inactive</span>";
        }
    }
    
    public String getApprovalStatusBadge() {
        if (approvalStatus == null) return "<span class='badge bg-secondary'>-</span>";
        
        switch (approvalStatus.toUpperCase()) {
            case "APPROVED":
                return "<span class='badge bg-success'>Approved</span>";
            case "REJECTED":
                return "<span class='badge bg-danger'>Rejected</span>";
            case "PENDING":
                return "<span class='badge bg-warning'>Pending</span>";
            default:
                return "<span class='badge bg-secondary'>" + approvalStatus + "</span>";
        }
    }
    
    public String getInterestMethodDisplay() {
        if (interestMethod == null) return "-";
        switch (interestMethod.toUpperCase()) {
            case "FLAT":
                return "Flat";
            case "EFFECTIVE":
                return "Effective";
            case "ANNUITY":
                return "Annuity";
            default:
                return interestMethod;
        }
    }
    
    public String getAdminTypeDisplay() {
        if (adminType == null) return "-";
        switch (adminType.toUpperCase()) {
            case "FIXED":
                return "Fixed";
            case "PERCENTAGE":
                return "Percentage";
            default:
                return adminType;
        }
    }
    
    public String getProvisionTypeDisplay() {
        if (provisionType == null) return "-";
        switch (provisionType.toUpperCase()) {
            case "FIXED":
                return "Fixed";
            case "PERCENTAGE":
                return "Percentage";
            default:
                return provisionType;
        }
    }
}