package com.project.gl.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import com.project.gl.model.enums.ReversalRequestStatus;

@Entity
@Table(
    name = "reversal_request",
    indexes = {
        @Index(
            name = "idx_reversal_request_journal",
            columnList = "original_journal_id"
        ),
        @Index(
            name = "idx_reversal_request_status",
            columnList = "status"
        ),
        @Index(
            name = "idx_reversal_request_requested_at",
            columnList = "requested_at"
        )
    }
)
@Getter
@Setter
public class ReversalRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        name = "original_journal_id",
        nullable = false
    )
    private Long originalJournalId;

    @Column(
        name = "requested_by",
        nullable = false,
        length = 100
    )
    private String requestedBy;

    @Column(
        name = "requested_at",
        nullable = false
    )
    private LocalDateTime requestedAt;

    @Column(
        name = "reason",
        nullable = false,
        length = 100
    )
    private String reason;

    @Column(
        name = "description",
        columnDefinition = "TEXT"
    )
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 20
    )
    private ReversalRequestStatus status;

    @Column(
        name = "approved_by",
        length = 100
    )
    private String approvedBy;

    @Column(
        name = "approved_at"
    )
    private LocalDateTime approvedAt;

    @Column(
        name = "rejected_by",
        length = 100
    )
    private String rejectedBy;

    @Column(
        name = "rejected_at"
    )
    private LocalDateTime rejectedAt;

    @Column(
        name = "rejection_reason",
        length = 1000
    )
    private String rejectionReason;
}