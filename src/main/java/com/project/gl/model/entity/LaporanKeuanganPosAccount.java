package com.project.gl.model.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "laporan_keuangan_pos_account",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_laporan_pos_account",
            columnNames = {"laporan_pos_id", "gl_account_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class LaporanKeuanganPosAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "laporan_pos_id", nullable = false)
    private Long laporanPosId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "laporan_pos_id",
        referencedColumnName = "id",
        insertable = false,
        updatable = false
    )
    private LaporanKeuanganPos laporanPos;

    @Column(name = "gl_account_id", nullable = false)
    private Integer glAccountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "gl_account_id",
        referencedColumnName = "id",
        insertable = false,
        updatable = false
    )
    private NomorGlAccount glAccount;
}