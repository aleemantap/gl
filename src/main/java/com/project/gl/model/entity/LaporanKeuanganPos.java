package com.project.gl.model.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "laporan_keuangan_pos",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_laporan_pos_kode",
            columnNames = {"kode_pos", "jenis_laporan"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class LaporanKeuanganPos extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kode_pos", length = 50, nullable = false)
    private String kodePos;

    @Column(name = "nama_pos", length = 150, nullable = false)
    private String namaPos;

    @Enumerated(EnumType.STRING)
    @Column(name = "jenis_laporan", length = 30, nullable = false)
    private JenisLaporanKeuangan jenisLaporan;

    @Column(name = "parent_id")
    private Long parentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "parent_id",
        referencedColumnName = "id",
        insertable = false,
        updatable = false
    )
    private LaporanKeuanganPos parent;

    @Column(name = "urutan", nullable = false)
    private Integer urutan = 0;

    @Column(name = "is_total", nullable = false)
    private Boolean isTotal = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}