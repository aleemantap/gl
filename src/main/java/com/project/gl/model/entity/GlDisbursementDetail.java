package com.project.gl.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "gl_disbursement_detail",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_disbursement_journal", columnNames = {"journal_header_id"}),
        @UniqueConstraint(name = "uk_disbursement_transaction", columnNames = {"transaction_no"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlDisbursementDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "journal_header_id", nullable = false, unique = true)
    private Long journalHeaderId;

    @Column(name = "transaction_no", length = 100, nullable = false, unique = true)
    private String transactionNo;

    @Builder.Default
    @Column(name = "admin_in_advance", nullable = false)
    private Boolean adminInAdvance = false;

    @Builder.Default
    @Column(name = "provision_in_advance", nullable = false)
    private Boolean provisionInAdvance = false;

    @Column(name = "bank_code", length = 20)
    private String bankCode;

    @Builder.Default
    @Column(name = "customer_bank", precision = 18, scale = 2, nullable = false)
    private BigDecimal customerBank = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}