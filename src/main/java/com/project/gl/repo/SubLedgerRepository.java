package com.project.gl.repo;

import com.project.gl.model.entity.JournalDetail;
import com.project.gl.model.entity.TransactionCodeJournal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface SubLedgerRepository extends JpaRepository<JournalDetail, Long> {


    @Query(value = """
        SELECT
            jh.transaction_date,
            jh.journal_no,
            jh.transaction_no,
            jd.dk,
            jd.amount,
            jd.description
        FROM journal_detail jd
        JOIN journal_header jh
            ON jh.id = jd.journal_header_id
        WHERE jd.gl_account_id = :glAccountId
        AND jh.transaction_date >= :startDate
        AND jh.transaction_date < :endDate
        ORDER BY
            jh.transaction_date ASC,
            jd.id ASC
        """, nativeQuery = true)
    List<Object[]> findDetails(
            @Param("glAccountId") Integer glAccountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


    @Query(value = """
        SELECT
            COALESCE(
                SUM(
                    CASE
                        WHEN jd.dk = nga.saldo_normal
                            THEN jd.amount
                        ELSE -jd.amount
                    END
                ),
                0
            )
        FROM journal_detail jd
        JOIN journal_header jh
            ON jh.id = jd.journal_header_id
        JOIN nomor_gl_account nga
            ON nga.id = jd.gl_account_id
        WHERE jd.gl_account_id = :glAccountId
        AND jh.transaction_date < :startDate
        """, nativeQuery = true)
    BigDecimal findOpeningBalance(
            @Param("glAccountId") Integer glAccountId,
            @Param("startDate") LocalDateTime startDate
    );
    

    @Query(value = """
        SELECT
            COALESCE(
                SUM(
                    CASE
                        WHEN jd.dk = nga.saldo_normal
                            THEN jd.amount
                        ELSE -jd.amount
                    END
                ),
                0
            )
        FROM journal_detail jd
        JOIN journal_header jh
            ON jh.id = jd.journal_header_id
        JOIN nomor_gl_account nga
            ON nga.id = jd.gl_account_id
        WHERE jd.gl_account_id = :glAccountId
        AND jh.transaction_date < :endDate
        """, nativeQuery = true)
    BigDecimal findGlBalance(
            @Param("glAccountId") Integer glAccountId,
            @Param("endDate") LocalDateTime endDate
    );
}
