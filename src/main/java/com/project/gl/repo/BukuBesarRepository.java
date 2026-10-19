package com.project.gl.repo;

import com.project.gl.model.entity.JournalDetail;
import com.project.gl.model.projection.BukuBesarProjection;
import com.project.gl.model.projection.SaldoAwalProjection;
import com.project.gl.model.projection.SaldoPeriodeProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BukuBesarRepository
        extends JpaRepository<JournalDetail, Long> {

    @Query(value = """
    SELECT
        jd.id AS detailId,
        jh.id AS journalHeaderId,
        jh.transaction_date AS transactionDate,
        jh.journal_no AS journalNo,
        jh.transaction_no AS transactionNo,
        kt.kode AS transactionCode,
        cb.kode_cabang AS branchCode,
        cb.nama_cabang AS branchName,
        jd.gl_account_id AS glAccountId,
        ga.nomor_gl AS accountCode,
        ga.nama_gl_account AS accountName,
        ga.saldo_normal AS normalBalance,
        jd.dk AS dk,
        jd.amount AS amount,
        jd.description AS description

    FROM journal_detail jd
    INNER JOIN journal_header jh ON jh.id = jd.journal_header_id
    INNER JOIN kode_transaksi kt ON kt.id = jh.transaction_code_id
    INNER JOIN cabang cb ON cb.id = jh.cabang_id
    INNER JOIN nomor_gl_account ga ON ga.id = jd.gl_account_id
    WHERE jh.status = 'POSTED'  
      AND (:idsEmpty = true OR jd.id IN (:ids))
      AND jd.gl_account_id = :glAccountId
      AND jh.transaction_date >= :tanggalDari
      AND jh.transaction_date < :tanggalSampai
      AND (
            :branchCode IS NULL
            OR :branchCode = ''
            OR cb.kode_cabang = :branchCode
          )
    ORDER BY
        jh.transaction_date ASC,
        jh.id ASC,
        jd.id ASC
    """,
    nativeQuery = true)
    List<BukuBesarProjection> findBukuBesar(
            @Param("ids") List<Long> ids,
            @Param("idsEmpty") boolean idsEmpty,
            @Param("glAccountId") Long glAccountId,
            @Param("tanggalDari") LocalDateTime tanggalDari,
            @Param("tanggalSampai") LocalDateTime tanggalSampai,
            @Param("branchCode") String branchCode
    );

    @Query(value = """
    SELECT

        COALESCE(SUM(
            CASE
                WHEN jd.dk = 'D'
                THEN jd.amount
                ELSE 0
            END
        ), 0) AS totalDebit,

        COALESCE(SUM(
            CASE
                WHEN jd.dk = 'K'
                THEN jd.amount
                ELSE 0
            END
        ), 0) AS totalCredit,

        MAX(ga.saldo_normal) AS normalBalance

    FROM journal_detail jd

    INNER JOIN journal_header jh
        ON jh.id = jd.journal_header_id

    INNER JOIN nomor_gl_account ga
        ON ga.id = jd.gl_account_id

    WHERE jh.status = 'POSTED'

      AND jd.gl_account_id = :glAccountId

      AND jh.transaction_date < :tanggalDari

      AND (
            :branchCode IS NULL
            OR :branchCode = ''
            OR jh.cabang_id = (
                SELECT cb.id
                FROM cabang cb
                WHERE cb.kode_cabang = :branchCode
            )
          )
    """,
    nativeQuery = true)
SaldoAwalProjection findSaldoAwal(
        @Param("glAccountId") Long glAccountId,
        @Param("tanggalDari") LocalDateTime tanggalDari,
        @Param("branchCode") String branchCode
);

    @Query(value = """
        SELECT

            COALESCE(SUM(
                CASE
                    WHEN jd.dk = 'D'
                    THEN jd.amount
                    ELSE 0
                END
            ), 0) AS totalDebit,

            COALESCE(SUM(
                CASE
                    WHEN jd.dk = 'K'
                    THEN jd.amount
                    ELSE 0
                END
            ), 0) AS totalCredit

        FROM journal_detail jd

        INNER JOIN journal_header jh
            ON jh.id = jd.journal_header_id

        WHERE jh.status = 'POSTED'

        AND jd.gl_account_id = :glAccountId

        AND jh.transaction_date >= :tanggalDari

        AND jh.transaction_date < :tanggalSampai

        AND (
                :branchCode IS NULL
                OR :branchCode = ''
                OR jh.cabang_id = (
                    SELECT cb.id
                    FROM cabang cb
                    WHERE cb.kode_cabang = :branchCode
                )
            )
        """,
        nativeQuery = true
    )
    SaldoPeriodeProjection findTotalPeriode(
            @Param("glAccountId") Long glAccountId,
            @Param("tanggalDari") LocalDateTime tanggalDari,
            @Param("tanggalSampai") LocalDateTime tanggalSampai,
            @Param("branchCode") String branchCode
    );

}