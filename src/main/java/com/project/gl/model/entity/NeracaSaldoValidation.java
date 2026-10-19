package com.project.gl.model.entity;

import java.time.LocalDateTime;

import com.project.gl.model.enums.NeracaSaldoStatus;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "neraca_saldo_validation",
    indexes = {
        @Index(
            name = "idx_nsv_submission",
            columnList = "submission_id"
        ),
        @Index(
            name = "idx_nsv_validated_at",
            columnList = "validated_at"
        )
    }
)
@Getter
@Setter
public class NeracaSaldoValidation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "submission_id", nullable = false)
    private Long submissionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private NeracaSaldoStatus status;

    @Column(name = "validated_by", nullable = false, length = 100)
    private String validatedBy;

    @Column(name = "validated_at", nullable = false)
    private LocalDateTime validatedAt;

    @Column(name = "note", length = 1000)
    private String note;
}