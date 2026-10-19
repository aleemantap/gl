package com.project.gl.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

import com.project.gl.model.enums.KeputusanStatus;

@Entity
@Table(
    name = "keputusan",
    indexes = {
        @Index(
            name = "idx_keputusan_status",
            columnList = "status"
        ),
        @Index(
            name = "idx_keputusan_kategori",
            columnList = "kategori"
        ),
        @Index(
            name = "idx_keputusan_tanggal",
            columnList = "tanggal"
        )
    }
)
@Getter
@Setter
public class Keputusan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        name = "judul",
        nullable = false,
        length = 255
    )
    private String judul;

    @Column(
        name = "deskripsi",
        columnDefinition = "TEXT"
    )
    private String deskripsi;

    @Column(
        name = "kategori",
        nullable = false,
        length = 30
    )
    private String kategori;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 20
    )
    private KeputusanStatus status;

    @Column(
        name = "owner_name",
        length = 100
    )
    private String ownerName;

    @Column(
        name = "tanggal",
        nullable = false
    )
    private LocalDate tanggal;

    @Column(
        name = "rule_reference",
        length = 100
    )
    private String ruleReference;
}