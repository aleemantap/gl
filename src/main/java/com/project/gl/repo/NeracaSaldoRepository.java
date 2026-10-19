package com.project.gl.repo;
import com.project.gl.model.entity.JournalDetail;
import com.project.gl.model.projection.NeracaSaldoProjection;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
public interface NeracaSaldoRepository  extends JpaRepository<JournalDetail, Long> {
 
    @Query(value = """
        SELECT
            ga.id AS glAccountId,
            ga.nomor_gl AS accountCode,
            ga.nama_gl_account AS accountName,
            ga.saldo_normal AS normalBalance,

            COALESCE(SUM(
                CASE
                    WHEN jh.transaction_date < :tanggalDari
                     AND jd.dk = 'D'
                    THEN jd.amount
                    ELSE 0
                END
            ), 0) AS debitSebelum,

            COALESCE(SUM(
                CASE
                    WHEN jh.transaction_date < :tanggalDari
                     AND jd.dk = 'K'
                    THEN jd.amount
                    ELSE 0
                END
            ), 0) AS kreditSebelum,

            COALESCE(SUM(
                CASE
                    WHEN jh.transaction_date >= :tanggalDari
                     AND jh.transaction_date < :tanggalSampai
                     AND jd.dk = 'D'
                    THEN jd.amount
                    ELSE 0
                END
            ), 0) AS totalDebit,

            COALESCE(SUM(
                CASE
                    WHEN jh.transaction_date >= :tanggalDari
                     AND jh.transaction_date < :tanggalSampai
                     AND jd.dk = 'K'
                    THEN jd.amount
                    ELSE 0
                END
            ), 0) AS totalKredit

        FROM nomor_gl_account ga
        INNER JOIN journal_detail jd  ON jd.gl_account_id = ga.id
        INNER JOIN journal_header jh  ON jh.id = jd.journal_header_id
        WHERE jh.status = 'POSTED'
          AND (:idsEmpty = true OR  ga.id  IN (:ids))
          AND jh.transaction_date < :tanggalSampai

          AND (
                :branchCode IS NULL
                OR :branchCode = ''
                OR EXISTS (
                    SELECT 1
                    FROM cabang cb
                    WHERE cb.id = jh.cabang_id
                      AND cb.kode_cabang = :branchCode
                )
              )

        GROUP BY
            ga.id,
            ga.nomor_gl,
            ga.nama_gl_account,
            ga.saldo_normal

        ORDER BY
            ga.nomor_gl
        """, nativeQuery = true)
    List<NeracaSaldoProjection> findNeracaSaldo(
            @Param("ids") List<Long> ids,
            @Param("idsEmpty") boolean idsEmpty,
            @Param("tanggalDari") LocalDateTime tanggalDari,
            @Param("tanggalSampai") LocalDateTime tanggalSampai,
            @Param("branchCode") String branchCode
    );
    

}
