package com.project.gl.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "transaction_collateral",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_transaction_collateral_transaction_no",
            columnNames = "transaction_no"
        )
    }
)
public class TransactionCollateral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_no", nullable = false, length = 100)
    private String transactionNo;

    @Column(name = "collateral_type", nullable = false, length = 50)
    private String collateralType;

    // @Column(name = "appraised_value", nullable = false, precision = 18, scale = 2)
    // private BigDecimal appraisedValue;

    // @Column(name = "ltv", nullable = false, precision = 5, scale = 2)
    // private BigDecimal ltv;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getCollateralType() {
        return collateralType;
    }

    public void setCollateralType(String collateralType) {
        this.collateralType = collateralType;
    }

    // public BigDecimal getAppraisedValue() {
    //     return appraisedValue;
    // }

    // public void setAppraisedValue(BigDecimal appraisedValue) {
    //     this.appraisedValue = appraisedValue;
    // }

    // public BigDecimal getLtv() {
    //     return ltv;
    // }

    // public void setLtv(BigDecimal ltv) {
    //     this.ltv = ltv;
    // }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}