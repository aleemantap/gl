package com.project.gl.repo;

import com.project.gl.model.entity.GlCashMovement;
import com.project.gl.model.projection.GlCashMovementProjection;
import com.project.gl.model.projection.RekonsiliasiKasTellerProjection;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GlCashMovementRepository extends JpaRepository<GlCashMovement, Long> {
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

        INNER JOIN journal_header jh ON jh.id = gcm.journal_header_id
        INNER JOIN kode_transaksi kt ON kt.id = jh.transaction_code_id
        INNER JOIN cabang cb ON cb.id = jh.cabang_id
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

        INNER JOIN journal_header jh ON jh.id = gcm.journal_header_id
        INNER JOIN kode_transaksi kt ON kt.id = jh.transaction_code_id
        INNER JOIN cabang cb ON cb.id = jh.cabang_id
        WHERE jh.status = 'POSTED'
        AND (:idsEmpty = true OR  gcm.id  IN (:ids))
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
        AND (:idsEmpty = true OR  gcm.id  IN (:ids))
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
    List<GlCashMovementProjection> findLogKustodiKasExport(
            @Param("ids") List<Long> ids,
            @Param("idsEmpty") boolean idsEmpty,
            @Param("tanggalDari") LocalDateTime tanggalDari,
            @Param("tanggalSampai") LocalDateTime tanggalSampai,
            @Param("branchCode") String branchCode,
            @Param("direction") String direction
          
    );


    //--- rekonsilasi =====================
     @Query(value = """
        SELECT

            :tellerUserId AS tellerUserId,

            :kasPointCode AS kasPointCode,

            COALESCE(
                SUM(
                    CASE
                        WHEN gcm.occurred_at < :tanggal
                             AND gcm.direction = 'IN'
                        THEN gcm.amount

                        WHEN gcm.occurred_at < :tanggal
                             AND gcm.direction = 'OUT'
                        THEN -gcm.amount

                        ELSE 0
                    END
                ),
                0
            ) AS saldoAwal,

            COALESCE(
                SUM(
                    CASE
                        WHEN gcm.occurred_at >= :tanggal
                             AND gcm.occurred_at < :tanggalBesok
                             AND gcm.direction = 'IN'
                        THEN gcm.amount
                        ELSE 0
                    END
                ),
                0
            ) AS totalIn,

            COALESCE(
                SUM(
                    CASE
                        WHEN gcm.occurred_at >= :tanggal
                             AND gcm.occurred_at < :tanggalBesok
                             AND gcm.direction = 'OUT'
                        THEN gcm.amount
                        ELSE 0
                    END
                ),
                0
            ) AS totalOut,

            (
                COALESCE(
                    SUM(
                        CASE
                            WHEN gcm.occurred_at < :tanggal
                                 AND gcm.direction = 'IN'
                            THEN gcm.amount

                            WHEN gcm.occurred_at < :tanggal
                                 AND gcm.direction = 'OUT'
                            THEN -gcm.amount

                            ELSE 0
                        END
                    ),
                    0
                )

                +

                COALESCE(
                    SUM(
                        CASE
                            WHEN gcm.occurred_at >= :tanggal
                                 AND gcm.occurred_at < :tanggalBesok
                                 AND gcm.direction = 'IN'
                            THEN gcm.amount
                            ELSE 0
                        END
                    ),
                    0
                )

                -

                COALESCE(
                    SUM(
                        CASE
                            WHEN gcm.occurred_at >= :tanggal
                                 AND gcm.occurred_at < :tanggalBesok
                                 AND gcm.direction = 'OUT'
                            THEN gcm.amount
                            ELSE 0
                        END
                    ),
                    0
                )
            ) AS saldoSistem

        FROM gl_cash_movement gcm

        INNER JOIN journal_header jh
            ON jh.id = gcm.journal_header_id

        WHERE jh.status = 'POSTED'

          AND gcm.teller_user_id = :tellerUserId

          AND gcm.kas_point_code = :kasPointCode

        """,
        nativeQuery = true)
    RekonsiliasiKasTellerProjection
    getRekonsiliasiKasTeller(
            @Param("tellerUserId") Long tellerUserId,
            @Param("kasPointCode") String kasPointCode,
            @Param("tanggal") LocalDateTime tanggal,
            @Param("tanggalBesok") LocalDateTime tanggalBesok
    );

    List<GlCashMovement> findByTellerUserIdAndKasPointCodeAndOccurredAtBetweenOrderByOccurredAtAsc(
            Long tellerUserId,
            String kasPointCode,
            LocalDateTime start,
            LocalDateTime end
    );

  

    List<GlCashMovement> findByJournalHeaderId(Long journalHeaderId);

    
}