package com.project.gl.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "gl_daily_closing",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_gl_daily_closing",
            columnNames = {"tanggal", "cabang_id"}
        )
    },
    indexes = {
        @Index(
            name = "idx_daily_closing_cabang",
            columnList = "cabang_id"
        ),
        @Index(
            name = "idx_daily_closing_status",
            columnList = "status"
        ),
        @Index(
            name = "idx_daily_closing_tanggal",
            columnList = "tanggal"
        )
    }
)
@Getter
@Setter
public class GlDailyClosing extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tanggal", nullable = false)
    private LocalDate tanggal;

    @Column(name = "cabang_id", nullable = false)
    private Integer cabangId;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "TERBUKA";

    @Column(name = "closed_by", length = 100)
    private String closedBy;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "reopened_by", length = 100)
    private String reopenedBy;

    @Column(name = "reopened_at")
    private LocalDateTime reopenedAt;

    @Column(name = "reopen_reason", length = 1000)
    private String reopenReason;
}