package com.project.gl.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.project.gl.model.enums.NeracaSaldoStatus;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "neraca_saldo_submission",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_ns_periode_unit",
            columnNames = {"periode", "unit_id"}
        )
    },
    indexes = {
        @Index(name = "idx_ns_periode", columnList = "periode"),
        @Index(name = "idx_ns_unit", columnList = "unit_id"),
        @Index(name = "idx_ns_status", columnList = "status")
    }
)
@Getter
@Setter
public class NeracaSaldoSubmission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "periode", nullable = false)
    private LocalDate periode;

    @Column(name = "unit_id", nullable = false)
    private Long unitId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private NeracaSaldoStatus status;

    @Column(name = "submitted_by", length = 100)
    private String submittedBy;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "validated_by", length = 100)
    private String validatedBy;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    @Column(name = "validation_note", length = 1000)
    private String validationNote;
}