package com.project.gl.repo;

import com.project.gl.model.entity.JournalDetail;
import com.project.gl.model.projection.GlCashBalanceProjection;
import com.project.gl.model.projection.GlCashMovementDetailProjection;
import com.project.gl.model.projection.GlCashMovementProjection;
import com.project.gl.model.projection.GlCashMovementTotalProjection;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JournalDetailRepository  extends JpaRepository<JournalDetail, Long> {
    //----------- N E W -------------------------------

    List<JournalDetail> findByJournalHeaderId(Long journalHeaderId);

    List<JournalDetail> findByJournalHeaderIdOrderByIdAsc(Long journalHeaderId);

    boolean existsByGlAccountId(Long glAccountId);

    

     @Query(value = """
        SELECT
            gcm.id AS id,
            gcm.journal_header_id AS journalHeaderId,

            jh.transaction_date AS transactionDate,
            jh.journal_no AS journalNo,
            jh.transaction_no AS transactionNo,

            kt.kode AS transactionCode,
            cb.kode_cabang AS branchCode,

            gcm.direction AS direction,
            gcm.amount AS amount,

            gcm.teller_user_id AS tellerUserId,
            gcm.kas_point_code AS kasPointCode,

            gcm.counterparty_type AS counterpartyType,
            gcm.counterparty_ref AS counterpartyRef,
            gcm.counterparty_name AS counterpartyName,

            gcm.description AS description,
            gcm.occurred_at AS occurredAt

        FROM gl_cash_movement gcm

        INNER JOIN journal_header jh
            ON jh.id = gcm.journal_header_id

        INNER JOIN kode_transaksi kt
            ON kt.id = jh.transaction_code_id

        INNER JOIN cabang cb
            ON cb.id = jh.cabang_id

        WHERE jh.status = 'POSTED'

        AND (
            :tanggalDari IS NULL
            OR gcm.occurred_at >= :tanggalDari
        )

        AND (
            :tanggalSampai IS NULL
            OR gcm.occurred_at < :tanggalSampai
        )

        AND (
            :branchCode IS NULL
            OR :branchCode = ''
            OR cb.kode_cabang = :branchCode
        )

        AND (
            :direction IS NULL
            OR :direction = ''
            OR gcm.direction = :direction
        )

        ORDER BY
            gcm.occurred_at DESC,
            gcm.id DESC
        """,
        countQuery = """
        SELECT COUNT(*)

        FROM gl_cash_movement gcm

        INNER JOIN journal_header jh
            ON jh.id = gcm.journal_header_id

        INNER JOIN cabang cb
            ON cb.id = jh.cabang_id

        WHERE jh.status = 'POSTED'

        AND (
            :tanggalDari IS NULL
            OR gcm.occurred_at >= :tanggalDari
        )

        AND (
            :tanggalSampai IS NULL
            OR gcm.occurred_at < :tanggalSampai
        )

        AND (
            :branchCode IS NULL
            OR :branchCode = ''
            OR cb.kode_cabang = :branchCode
        )

        AND (
            :direction IS NULL
            OR :direction = ''
            OR gcm.direction = :direction
        )
        """,
        nativeQuery = true)
    Page<GlCashMovementProjection> findLogKustodiKas(
            @Param("tanggalDari") LocalDateTime tanggalDari,
            @Param("tanggalSampai") LocalDateTime tanggalSampai,
            @Param("branchCode") String branchCode,
            @Param("direction") String direction,
            Pageable pageable
    );


    @Query(value = """
        SELECT
            jh.cabang_id AS cabangId,
            jd.gl_account_id AS glAccountId,

            SUM(
                CASE
                    WHEN jd.dk = 'D' THEN jd.amount
                    WHEN jd.dk = 'K' THEN -jd.amount
                    ELSE 0
                END
            ) AS saldo

        FROM journal_detail jd

        INNER JOIN journal_header jh
            ON jh.id = jd.journal_header_id

        WHERE jh.status = 'POSTED'
        AND jh.cabang_id = :cabangId
        AND jh.transaction_date < :batasWaktu
        AND jd.gl_account_id IN (11, 34)

        GROUP BY
            jh.cabang_id,
            jd.gl_account_id
        """, nativeQuery = true)
    List<GlCashBalanceProjection> findSaldoKasByCabang(
        @Param("cabangId") Long cabangId,
        @Param("batasWaktu") LocalDateTime batasWaktu
    );


    @Query(value = """
    SELECT
        gcm.direction AS direction,
        SUM(gcm.amount) AS totalAmount
    FROM gl_cash_movement gcm
    INNER JOIN journal_header jh
        ON jh.id = gcm.journal_header_id
    WHERE jh.status = 'POSTED'
      AND jh.cabang_id = :cabangId
      AND jh.transaction_date >= :tanggalMulai
      AND jh.transaction_date < :tanggalSelesai
    GROUP BY gcm.direction
    """, nativeQuery = true)
    List<GlCashMovementTotalProjection> findTotalKasHarian(
        @Param("cabangId") Long cabangId,
        @Param("tanggalMulai") LocalDateTime tanggalMulai,
        @Param("tanggalSelesai") LocalDateTime tanggalSelesai
    );

    @Query(value = """
        SELECT
            kt.kode AS transactionCode,
            kt.nama_transaksi AS transactionName,
            gcm.direction AS direction,
            SUM(gcm.amount) AS totalAmount

        FROM gl_cash_movement gcm

        INNER JOIN journal_header jh
            ON jh.id = gcm.journal_header_id

        INNER JOIN kode_transaksi kt
            ON kt.id = jh.transaction_code_id

        WHERE jh.status = 'POSTED'
        AND jh.cabang_id = :cabangId
        AND jh.transaction_date >= :tanggalMulai
        AND jh.transaction_date < :tanggalSelesai

        GROUP BY
            kt.kode,
            kt.nama_transaksi,
            gcm.direction

        ORDER BY
            kt.kode,
            gcm.direction
        """, nativeQuery = true)
    List<GlCashMovementDetailProjection> findDetailKasHarian(
        @Param("cabangId") Long cabangId,
        @Param("tanggalMulai") LocalDateTime tanggalMulai,
        @Param("tanggalSelesai") LocalDateTime tanggalSelesai
    );
}