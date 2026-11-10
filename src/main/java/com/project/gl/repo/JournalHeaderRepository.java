package com.project.gl.repo;


import com.project.gl.model.entity.JournalHeader;
import com.project.gl.model.projection.JournalListProjection;
import com.project.gl.model.projection.JournalPeriodSummaryProjection;
import com.project.gl.model.projection.OjkProfitLossProjection;

import java.time.LocalDateTime;
import java.util.List;
// import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;

public interface JournalHeaderRepository extends JpaRepository<JournalHeader, Long> {
    Optional<JournalHeader> findByIdempotencyKey(String idempotencyKey);
    Optional<JournalHeader> findByJournalNo(String journalNo);
    boolean existsByReversalOfJournalId(Long reversalOfJournalId);
    boolean existsByJournalId(Long journalId);
   // boolean existsByReversalOfJournalId(Long reversalOfJournalId);
    // DAFTAR JURNAL
        @Query(
        value = """
                SELECT
                jh.id AS id,
                jh.journal_no AS journalNo,
                jh.transaction_no AS transactionNo,
                kt.kode AS transactionCode,
                c.kode_cabang AS branchCode,
                jh.transaction_date AS transactionDate,
                COALESCE(SUM(CASE WHEN jd.dk = 'D' THEN jd.amount ELSE 0 END), 0) AS totalDebit,
                COALESCE(SUM(CASE WHEN jd.dk = 'K' THEN jd.amount ELSE 0 END), 0) AS totalCredit,
                jh.status AS status,
                CASE
                        WHEN jh.reversal_of_journal_id IS NOT NULL
                                THEN 'REVERSAL'

                        WHEN EXISTS (
                                SELECT 1
                                FROM journal_header rh
                                WHERE rh.reversal_of_journal_id = jh.id
                        )
                                THEN 'SUDAH DIREVERSAL'

                        ELSE 'NORMAL'
                END AS reversalStatus,
                c.nama_cabang
                FROM journal_header jh
                JOIN journal_detail jd ON jd.journal_header_id = jh.id
                JOIN kode_transaksi kt ON kt.id = jh.transaction_code_id
                JOIN cabang c ON c.id = jh.cabang_id
                WHERE
              
                (:fromDate IS NULL OR jh.transaction_date >= :fromDate)
                AND (:toDate IS NULL OR jh.transaction_date < :toDate)
                AND (:branchCode IS NULL OR :branchCode = '' OR c.kode_cabang = :branchCode)
                AND (:transactionCode IS NULL OR :transactionCode = '' OR kt.kode = :transactionCode)
                AND (:journalNo IS NULL OR :journalNo = '' OR jh.journal_no LIKE CONCAT('%', :journalNo, '%'))
                AND (:transactionNo IS NULL OR :transactionNo = '' OR jh.transaction_no LIKE CONCAT('%', :transactionNo, '%'))
                AND (:status IS NULL OR :status = '' OR jh.status = :status)

                AND (
                        :statusReversal IS NULL
                        OR :statusReversal = ''
                        OR (
                        CASE
                                WHEN jh.reversal_of_journal_id IS NOT NULL
                                THEN 'REVERSAL'

                                WHEN EXISTS (
                                SELECT 1
                                FROM journal_header rh
                                WHERE rh.reversal_of_journal_id = jh.id
                                )
                                THEN 'SUDAH DIREVERSAL'

                                ELSE 'NORMAL'
                        END
                        ) = :statusReversal
                )

                GROUP BY
                jh.id,
                jh.journal_no,
                jh.transaction_no,
                kt.kode,
                c.kode_cabang,
                jh.transaction_date,
                jh.status,
                jh.reversal_of_journal_id
                ORDER BY jh.transaction_date DESC, jh.id DESC
                """,
        countQuery = """
                SELECT COUNT(DISTINCT jh.id)
                FROM journal_header jh
                JOIN journal_detail jd ON jd.journal_header_id = jh.id
                JOIN kode_transaksi kt ON kt.id = jh.transaction_code_id
                JOIN cabang c ON c.id = jh.cabang_id
                WHERE
              
                (:fromDate IS NULL OR jh.transaction_date >= :fromDate)
                AND (:toDate IS NULL OR jh.transaction_date < :toDate)
                AND (:branchCode IS NULL OR :branchCode = '' OR c.kode_cabang = :branchCode)
                AND (:transactionCode IS NULL OR :transactionCode = '' OR kt.kode = :transactionCode)
                AND (:journalNo IS NULL OR :journalNo = '' OR jh.journal_no LIKE CONCAT('%', :journalNo, '%'))
                AND (:transactionNo IS NULL OR :transactionNo = '' OR jh.transaction_no LIKE CONCAT('%', :transactionNo, '%'))
                AND (:status IS NULL OR :status = '' OR jh.status = :status)
                AND (
                        :statusReversal IS NULL
                        OR :statusReversal = ''
                        OR (
                                CASE
                                WHEN jh.reversal_of_journal_id IS NOT NULL
                                        THEN 'REVERSAL'

                                WHEN EXISTS (
                                        SELECT 1
                                        FROM journal_header rh
                                        WHERE rh.reversal_of_journal_id = jh.id
                                )
                                        THEN 'SUDAH DIREVERSAL'

                                ELSE 'NORMAL'
                                END
                        ) = :statusReversal
                )
                """,
        nativeQuery = true
        )
        Page<JournalListProjection> findJournalList(
                @Param("fromDate") LocalDateTime fromDate,
                @Param("toDate") LocalDateTime toDate,
                @Param("branchCode") String branchCode,
                @Param("transactionCode") String transactionCode,
                @Param("journalNo") String journalNo,
                @Param("transactionNo") String transactionNo,
                @Param("status") String status,
                @Param("statusReversal") String statusReversal,
                Pageable pageable
        );

        // =========================================================================
        // KHUSUS EXPORT (Mengembalikan List tanpa Pagination)
        // =========================================================================
        @Query(
        value = """
                SELECT
                jh.id AS id,
                jh.journal_no AS journalNo,
                jh.transaction_no AS transactionNo,
                kt.kode AS transactionCode,
                c.kode_cabang AS branchCode,
                jh.transaction_date AS transactionDate,
                COALESCE(SUM(CASE WHEN jd.dk = 'D' THEN jd.amount ELSE 0 END), 0) AS totalDebit,
                COALESCE(SUM(CASE WHEN jd.dk = 'K' THEN jd.amount ELSE 0 END), 0) AS totalCredit,
                jh.status AS status,
                CASE
                        WHEN jh.reversal_of_journal_id IS NOT NULL THEN 'REVERSAL'
                        WHEN EXISTS (
                                SELECT 1 FROM journal_header rh WHERE rh.reversal_of_journal_id = jh.id
                        ) THEN 'SUDAH DIREVERSAL'
                        ELSE 'NORMAL'
                END AS reversalStatus,
                c.nama_cabang AS namaCabang
                FROM journal_header jh
                JOIN journal_detail jd ON jd.journal_header_id = jh.id
                JOIN kode_transaksi kt ON kt.id = jh.transaction_code_id
                JOIN cabang c ON c.id = jh.cabang_id
                WHERE
                (:#{#listIds == null} = true OR jh.id IN (:listIds))
                AND (:fromDate IS NULL OR jh.transaction_date >= :fromDate)
                AND (:toDate IS NULL OR jh.transaction_date < :toDate)
                AND (:branchCode IS NULL OR :branchCode = '' OR c.kode_cabang = :branchCode)
                AND (:transactionCode IS NULL OR :transactionCode = '' OR kt.kode = :transactionCode)
                AND (:journalNo IS NULL OR :journalNo = '' OR jh.journal_no LIKE CONCAT('%', :journalNo, '%'))
                AND (:transactionNo IS NULL OR :transactionNo = '' OR jh.transaction_no LIKE CONCAT('%', :transactionNo, '%'))
                AND (:status IS NULL OR :status = '' OR jh.status = :status)
                AND (
                        :statusReversal IS NULL
                        OR :statusReversal = ''
                        OR (
                                CASE
                                WHEN jh.reversal_of_journal_id IS NOT NULL THEN 'REVERSAL'
                                WHEN EXISTS (
                                        SELECT 1 FROM journal_header rh WHERE rh.reversal_of_journal_id = jh.id
                                ) THEN 'SUDAH DIREVERSAL'
                                ELSE 'NORMAL'
                                END
                        ) = :statusReversal
                )
                GROUP BY
                jh.id,
                jh.journal_no,
                jh.transaction_no,
                kt.kode,
                c.kode_cabang,
                c.nama_cabang,
                jh.transaction_date,
                jh.status,
                jh.reversal_of_journal_id
                ORDER BY jh.transaction_date DESC, jh.id DESC
                """,
        nativeQuery = true
        )
        List<JournalListProjection> findJournalListForExport(
                @Param("listIds") List<Long> listIds,
                @Param("fromDate") LocalDateTime fromDate,
                @Param("toDate") LocalDateTime toDate,
                @Param("branchCode") String branchCode,
                @Param("transactionCode") String transactionCode,
                @Param("journalNo") String journalNo,
                @Param("transactionNo") String transactionNo,
                @Param("status") String status,
                @Param("statusReversal") String statusReversal
        );

        @Query(value = """
                SELECT COUNT(*)
                FROM journal_header
                WHERE idempotency_key = :idempotencyKey
                AND status = 'POSTED'
        """, nativeQuery = true)
        long countPostedByIdempotencyKey(
        @Param("idempotencyKey") String idempotencyKey
        );


       
        // SUMMARY JURNAL UNTUK TUTUP BUKU
        @Query(value = """
        SELECT
                COALESCE(
                SUM(CASE WHEN jd.dk = 'D' THEN jd.amount ELSE 0 END),
                0
                ) AS totalDebit,

                COALESCE(
                SUM(CASE WHEN jd.dk = 'K' THEN jd.amount ELSE 0 END),
                0
                ) AS totalKredit,

                COUNT(DISTINCT jh.id) AS totalJournal

        FROM journal_header jh

        LEFT JOIN journal_detail jd
                ON jd.journal_header_id = jh.id

        WHERE jh.status = 'POSTED'
        AND jh.cabang_id = :cabangId
        AND jh.transaction_date >= :tanggalMulai
        AND jh.transaction_date < :tanggalSelesai
        """, nativeQuery = true)
        JournalPeriodSummaryProjection findPeriodSummary(
        @Param("cabangId") Long cabangId,
        @Param("tanggalMulai") LocalDateTime tanggalMulai,
        @Param("tanggalSelesai") LocalDateTime tanggalSelesai
        );

        // PROFIT LOSS UNTUK PERHITUNGAN TOTAL EQUITY OJK
        @Query(value = """
        SELECT
                COALESCE(
                SUM(
                        CASE
                        WHEN gat.name = 'revenue' THEN
                                CASE
                                WHEN na.saldo_normal = 'K'
                                        THEN jd.amount
                                ELSE -jd.amount
                                END
                        ELSE 0
                        END
                ), 0
                ) AS revenueAmount,

                COALESCE(
                SUM(
                        CASE
                        WHEN gat.name = 'expense' THEN
                                CASE
                                WHEN na.saldo_normal = 'D'
                                        THEN jd.amount
                                ELSE -jd.amount
                                END
                        ELSE 0
                        END
                ), 0
                ) AS expenseAmount

        FROM journal_header jh
        JOIN journal_detail jd
                ON jd.journal_header_id = jh.id
        JOIN nomor_gl_account na
                ON na.id = jd.gl_account_id
        JOIN gl_account_type gat
                ON gat.id = na.type_gl_account_id

        WHERE jh.status = 'POSTED'
        AND jh.transaction_date >= :tanggalMulai
        AND jh.transaction_date < :tanggalSelesai
        AND gat.name IN ('revenue', 'expense')
        """, nativeQuery = true)
        OjkProfitLossProjection findProfitLossForOjk(
        @Param("tanggalMulai") LocalDateTime tanggalMulai,
        @Param("tanggalSelesai") LocalDateTime tanggalSelesai
        ); 
}
