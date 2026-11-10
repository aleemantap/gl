package com.project.gl.model.entity;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.project.gl.model.enums.GlPeriodClosingStatus;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "gl_period_closing",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_gl_period_closing",
            columnNames = {"tahun", "bulan", "cabang_id"}
        )
    },
    indexes = {
        @Index(name = "idx_period_closing_cabang", columnList = "cabang_id"),
        @Index(name = "idx_period_closing_status", columnList = "status")
    }
)
@Getter
@Setter
public class GlPeriodClosing extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tahun", nullable = false)
    private Integer tahun;

    @Column(name = "bulan", nullable = false)
    private Integer bulan;

    @Column(name = "cabang_id", nullable = false)
    private Integer cabangId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private GlPeriodClosingStatus status = GlPeriodClosingStatus.TERBUKA;

    @Column(name = "total_debit", precision = 18, scale = 2)
    private BigDecimal totalDebit = BigDecimal.ZERO;

    @Column(name = "total_kredit", precision = 18, scale = 2)
    private BigDecimal totalKredit = BigDecimal.ZERO;

    @Column(name = "is_neraca_seimbang")
    private Boolean neracaSeimbang = false;

    @Column(name = "is_ckpn_created")
    private Boolean ckpnCreated = false;

    @Column(name = "pending_gadai_count")
    private Integer pendingGadaiCount = 0;

    @Column(name = "submitted_by", length = 100)
    private String submittedBy;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejected_by", length = 100)
    private String rejectedBy;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "rejection_reason", length = 1000)
    private String rejectionReason;

    @Column(name = "pending_gadai_checked_by", length = 100)
    private String pendingGadaiCheckedBy;

    @Column(name = "pending_gadai_checked_at")
    private LocalDateTime pendingGadaiCheckedAt;

    @Column(name = "pending_gadai_note", length = 1000)
    private String pendingGadaiNote;

   
}