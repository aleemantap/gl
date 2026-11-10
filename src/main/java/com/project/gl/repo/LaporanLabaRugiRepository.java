package com.project.gl.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.gl.model.entity.LaporanKeuanganPos;
import com.project.gl.model.projection.LaporanLabaRugiDetailProjection;
import com.project.gl.model.projection.LaporanLabaRugiProjection;

import java.time.LocalDateTime;
import java.util.List;

public interface LaporanLabaRugiRepository
        extends JpaRepository<LaporanKeuanganPos, Long> {

    @Query(value = """
        SELECT
            p.id AS posId,
            p.kode_pos AS kodePos,
            p.nama_pos AS namaPos,
            p.parent_id AS parentId,
            p.urutan AS urutan,

            COALESCE(
                SUM(
                    CASE
                        WHEN jh.transaction_date >= :bulanMulai
                         AND jh.transaction_date < :bulanSelesai
                        THEN
                            CASE
                                WHEN nga.saldo_normal = 'K' AND jd.dk = 'K'
                                    THEN jd.amount
                                WHEN nga.saldo_normal = 'K' AND jd.dk = 'D'
                                    THEN -jd.amount
                                WHEN nga.saldo_normal = 'D' AND jd.dk = 'D'
                                    THEN jd.amount
                                WHEN nga.saldo_normal = 'D' AND jd.dk = 'K'
                                    THEN -jd.amount
                                ELSE 0
                            END
                        ELSE 0
                    END
                ), 0
            ) AS bulanIni,

            COALESCE(
                SUM(
                    CASE
                        WHEN jh.transaction_date >= :tahunMulai
                         AND jh.transaction_date < :bulanSelesai
                        THEN
                            CASE
                                WHEN nga.saldo_normal = 'K' AND jd.dk = 'K'
                                    THEN jd.amount
                                WHEN nga.saldo_normal = 'K' AND jd.dk = 'D'
                                    THEN -jd.amount
                                WHEN nga.saldo_normal = 'D' AND jd.dk = 'D'
                                    THEN jd.amount
                                WHEN nga.saldo_normal = 'D' AND jd.dk = 'K'
                                    THEN -jd.amount
                                ELSE 0
                            END
                        ELSE 0
                    END
                ), 0
            ) AS tahunBerjalan

        FROM laporan_keuangan_pos p

        JOIN laporan_keuangan_pos_account m
            ON m.laporan_pos_id = p.id

        JOIN nomor_gl_account nga
            ON nga.id = m.gl_account_id

        LEFT JOIN journal_detail jd
            ON jd.gl_account_id = nga.id

        LEFT JOIN journal_header jh
            ON jh.id = jd.journal_header_id
           AND jh.status = 'POSTED'
           AND jh.transaction_date >= :tahunMulai
           AND jh.transaction_date < :bulanSelesai
           AND (:cabangId IS NULL OR jh.cabang_id = :cabangId)

        WHERE p.jenis_laporan = 'LABA_RUGI'
          AND p.is_active = 1
          AND p.is_total = 0
          AND p.parent_id IS NOT NULL

        GROUP BY
            p.id,
            p.kode_pos,
            p.nama_pos,
            p.parent_id,
            p.urutan

        ORDER BY
            p.parent_id,
            p.urutan
        """, nativeQuery = true)
    List<LaporanLabaRugiProjection> findLaporanLabaRugi(
        @Param("tahunMulai") LocalDateTime tahunMulai,
        @Param("bulanMulai") LocalDateTime bulanMulai,
        @Param("bulanSelesai") LocalDateTime bulanSelesai,
        @Param("cabangId") Long cabangId
    );

    @Query(value = """
        SELECT
            p.id AS posId,
            p.kode_pos AS kodePos,

            COALESCE(SUM(
                CASE
                    WHEN jh.id IS NOT NULL
                     AND jh.transaction_date >= :awalBulan
                     AND jh.transaction_date < :akhirBulan
                    THEN
                        CASE
                            WHEN nga.saldo_normal = 'K'
                                THEN CASE WHEN jd.dk = 'K'
                                    THEN jd.amount ELSE -jd.amount END
                            ELSE
                                CASE WHEN jd.dk = 'D'
                                    THEN jd.amount ELSE -jd.amount END
                        END
                    ELSE 0
                END
            ), 0) AS bulanIni,

            COALESCE(SUM(
                CASE
                    WHEN jh.id IS NOT NULL
                     AND jh.transaction_date >= :awalTahun
                     AND jh.transaction_date < :akhirBulan
                    THEN
                        CASE
                            WHEN nga.saldo_normal = 'K'
                                THEN CASE WHEN jd.dk = 'K'
                                    THEN jd.amount ELSE -jd.amount END
                            ELSE
                                CASE WHEN jd.dk = 'D'
                                    THEN jd.amount ELSE -jd.amount END
                        END
                    ELSE 0
                END
            ), 0) AS tahunBerjalan

        FROM laporan_keuangan_pos p

        LEFT JOIN laporan_keuangan_pos_account m
            ON m.laporan_pos_id = p.id

        LEFT JOIN nomor_gl_account nga
            ON nga.id = m.gl_account_id

        LEFT JOIN journal_detail jd
            ON jd.gl_account_id = nga.id

        LEFT JOIN journal_header jh
            ON jh.id = jd.journal_header_id
            AND jh.status = 'POSTED'
            AND (:cabangId IS NULL OR jh.cabang_id = :cabangId)

        WHERE p.jenis_laporan = 'LABA_RUGI'
          AND p.is_active = 1
          AND p.is_total = 0
          AND p.parent_id IS NOT NULL

        GROUP BY p.id, p.kode_pos
        ORDER BY p.id
        """, nativeQuery = true)
    List<LaporanLabaRugiProjection> findSaldoLabaRugi(
            @Param("awalBulan") LocalDateTime awalBulan,
            @Param("akhirBulan") LocalDateTime akhirBulan,
            @Param("awalTahun") LocalDateTime awalTahun,
            @Param("cabangId") Long cabangId
    );


    @Query(value = """
        SELECT
            jh.transaction_date AS tanggal,
            jh.journal_no AS nomorJurnal,
            jh.transaction_no AS nomorTransaksi,
            nga.nomor_gl AS nomorGl,
            nga.nama_gl_account AS namaGlAccount,
            jd.dk AS dk,

            CASE
                WHEN jd.dk = 'D' THEN jd.amount
                ELSE 0
            END AS debit,

            CASE
                WHEN jd.dk = 'K' THEN jd.amount
                ELSE 0
            END AS kredit,

            CASE
                WHEN nga.saldo_normal = 'K' AND jd.dk = 'K'
                    THEN jd.amount
                WHEN nga.saldo_normal = 'K' AND jd.dk = 'D'
                    THEN -jd.amount
                WHEN nga.saldo_normal = 'D' AND jd.dk = 'D'
                    THEN jd.amount
                WHEN nga.saldo_normal = 'D' AND jd.dk = 'K'
                    THEN -jd.amount
                ELSE 0
            END AS saldo

        FROM laporan_keuangan_pos p

        JOIN laporan_keuangan_pos_account m
            ON m.laporan_pos_id = p.id

        JOIN nomor_gl_account nga
            ON nga.id = m.gl_account_id

        JOIN journal_detail jd
            ON jd.gl_account_id = nga.id

        JOIN journal_header jh
            ON jh.id = jd.journal_header_id

        WHERE p.kode_pos = :kodePos
        AND p.jenis_laporan = 'LABA_RUGI'
        AND p.is_active = 1
        AND jh.status = 'POSTED'
        AND jh.transaction_date >= :awalPeriode
        AND jh.transaction_date < :akhirPeriode
        AND (:cabangId IS NULL OR jh.cabang_id = :cabangId)

        ORDER BY
            jh.transaction_date,
            jh.journal_no,
            jd.id
        """, nativeQuery = true)
    List<LaporanLabaRugiDetailProjection> findDetailJurnal(
            @Param("kodePos") String kodePos,
            @Param("awalPeriode") LocalDateTime awalPeriode,
            @Param("akhirPeriode") LocalDateTime akhirPeriode,
            @Param("cabangId") Long cabangId
    );

}

