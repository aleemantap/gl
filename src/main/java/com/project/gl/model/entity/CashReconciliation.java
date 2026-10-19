package com.project.gl.model.entity;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "cash_reconciliation",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_cash_reconciliation",
            columnNames = {
                "teller_user_id",
                "kas_point_code",
                "reconciliation_date"
            }
        )
    }
)
@Getter
@Setter
public class CashReconciliation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "teller_user_id", nullable = false)
    private Long tellerUserId;

    @Column(name = "kas_point_code", nullable = false, length = 50)
    private String kasPointCode;

    @Column(name = "reconciliation_date", nullable = false)
    private LocalDate reconciliationDate;

    @Column(name = "opening_balance", nullable = false)
    private BigDecimal openingBalance;

    @Column(name = "total_in", nullable = false)
    private BigDecimal totalIn;

    @Column(name = "total_out", nullable = false)
    private BigDecimal totalOut;

    @Column(name = "system_balance", nullable = false)
    private BigDecimal systemBalance;

    @Column(name = "physical_balance", nullable = false)
    private BigDecimal physicalBalance;

    @Column(name = "difference", nullable = false)
    private BigDecimal difference;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "reconciled_by", length = 50)
    private String reconciledBy;

    @Column(name = "reconciled_at")
    private LocalDateTime reconciledAt;

    @Column(length = 255)
    private String description;

   
}