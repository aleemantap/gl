package com.project.gl.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.project.gl.model.enums.OjkRatioRegion;
import com.project.gl.model.enums.OjkRatioStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "ojk_ratio_report",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_ojk_ratio_report_no",
            columnNames = "report_no"
        ),
        @UniqueConstraint(
            name = "uk_ojk_ratio_report_periode",
            columnNames = "periode"
        )
    }
)
@Getter
@Setter
public class OjkRatioReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_no", nullable = false, length = 50)
    private String reportNo;

    @Column(name = "periode", nullable = false)
    private LocalDate periode;

    @Enumerated(EnumType.STRING)
    @Column(name = "region_scope", nullable = false)
    private OjkRatioRegion regionScope;

    @Column(name = "total_equity", precision = 20, scale = 2)
    private BigDecimal totalEquity = BigDecimal.ZERO;

    @Column(name = "paid_up_capital", precision = 20, scale = 2)
    private BigDecimal paidUpCapital = BigDecimal.ZERO;

    @Column(name = "pawn_loan_amount", precision = 20, scale = 2)
    private BigDecimal pawnLoanAmount = BigDecimal.ZERO;

    @Column(name = "total_loan_amount", precision = 20, scale = 2)
    private BigDecimal totalLoanAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OjkRatioStatus status = OjkRatioStatus.DRAFT;

    @Column(name = "calculated_at")
    private LocalDateTime calculatedAt;

    @Column(name = "calculated_by", length = 100)
    private String calculatedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "reviewed_by", length = 100)
    private String reviewedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    @Column(length = 1000)
    private String notes;

    @OneToMany(
        mappedBy = "report",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<OjkRatioDetail> details = new ArrayList<>();

    public void addDetail(OjkRatioDetail detail) {
        details.add(detail);
        detail.setReport(this);
    }

    public void removeDetail(OjkRatioDetail detail) {
        details.remove(detail);
        detail.setReport(null);
    }
}