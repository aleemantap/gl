package com.project.gl.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_rule", indexes = {
    @Index(name = "idx_product_rule_product", columnList = "product_id"),
    @Index(name = "idx_product_rule_effective", columnList = "product_id, effective_from, effective_to")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   
    @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "product_id", nullable = false)
    @JoinColumn(name = "product_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Product product;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Builder.Default
    @Column(name = "rule_version", nullable = false)
    private Integer ruleVersion = 1;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    // @Column(name = "interest_rate", precision = 10, scale = 4)
    // private BigDecimal interestRate;

    // @Column(name = "interest_period_days")
    // private Integer interestPeriodDays;

    // @Column(name = "interest_method", length = 30)
    // private String interestMethod;

    // @Column(name = "admin_type", length = 20)
    // private String adminType;

    // @Column(name = "admin_value", precision = 18, scale = 2)
    // private BigDecimal adminValue;

    // @Column(name = "provision_type", length = 20)
    // private String provisionType;

    // @Column(name = "provision_value", precision = 18, scale = 4)
    // private BigDecimal provisionValue;

    @Column(name = "tenor_min_days")
    private Integer tenorMinDays;

    @Column(name = "tenor_max_days")
    private Integer tenorMaxDays;

    @Column(name = "auction_trigger_days")
    private Integer auctionTriggerDays;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "approval_status", nullable = false, length = 20)
    private String approvalStatus = "PENDING";

    @Column(name = "approved_by", length = 50)
    private String approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Builder.Default
    @Column(name = "source_system", length = 30)
    private String sourceSystem = "GADAI";
}