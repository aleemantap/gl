package com.project.gl.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import com.project.gl.model.enums.TransactionRejectionStatus;

@Entity
@Table(name = "transaction_rejection")
@Getter
@Setter
public class TransactionRejection extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTime;

    @Column(name = "typeTransaction", length = 50)
    private String typeTransaction;


    @Column(name = "cabang_id")
    private Long cabangId;

    @Column(name = "branch_code", length = 50)
    private String branchCode;

    @Column(
        name = "transaction_no",
        nullable = false,
        length = 100
    )
    private String transactionNo;

    @Column(name = "transaction_code", nullable = false, length = 100)
    private String transactionCode;

    @Column(name = "rejection_reason", nullable = false, length = 500)
    private String rejectionReason;

    @Column(name = "payload", columnDefinition = "json")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TransactionRejectionStatus status;

    // @Column(name = "created_at")
    // private LocalDateTime createdAt;

    // @Column(name = "created_by", length = 50)
    // private String createdBy;
}