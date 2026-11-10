package com.project.gl.model.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OjkRatioSourceData {

    // Data permodalan
    private BigDecimal totalEquity;
    private BigDecimal paidUpCapital;

    // Data pinjaman
    private BigDecimal pawnLoanAmount;
    private BigDecimal totalLoanAmount;

    // BMPP
    private BigDecimal totalRelatedPartyLoan;
    private List<CustomerLoanData> customerLoans;
    private List<CustomerGroupLoanData> customerGroupLoans;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerLoanData {
        private String customerRef;
        private String customerName;
        private boolean relatedParty;
        private BigDecimal outstanding;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerGroupLoanData {
        private String groupRef;
        private String groupName;
        private BigDecimal outstanding;
    }
}