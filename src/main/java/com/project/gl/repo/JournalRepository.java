package com.project.gl.repo;

import com.project.gl.model.entity.JournalId;
import com.project.gl.model.response.JournalIDResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
@Repository
public interface JournalRepository extends JpaRepository<JournalId, Long> {
    
    boolean existsByJournalCode(String journalCode);
    JournalId findByJournalCode(String journalCode);
    long countByJournalCode(String journalCode);    
    @Query("SELECT NEW com.project.gl.model.response.JournalIDResponse(" +
    "j.id, " +
    "j.journalCode, " +
    "j.description, " +
    "j.isActive, " +
    "j.createdAt, " +
    "j.updatedAt) " +
    "FROM JournalId j " +
    "WHERE j.id = :id")
    Optional<JournalIDResponse> findJournalIdWithDetailsById(@Param("id") Long id);
    List<JournalId> findAllByIsActiveTrueOrderByJournalCodeAsc();
    
   

    @Query("""
        SELECT NEW com.project.gl.model.response.JournalIDResponse(
            j.id,
            j.journalCode,
            j.description,
            j.isActive,
             COUNT(jlt.id) As totalAkun,
            j.createdAt,
            j.updatedAt
        )
        FROM JournalId j
        LEFT JOIN JournalLineTemplate jlt ON j.id = jlt.journalId
        GROUP BY j.id, j.journalCode, j.description, j.isActive, j.createdAt, j.updatedAt
    """)
    Page<JournalIDResponse> findAllJournalIdWithDetails(Pageable pageable);




    @Query(value = """
        SELECT COALESCE(
            SUM(
                CASE
                    WHEN jd.dk = 'K' THEN jd.amount
                    WHEN jd.dk = 'D' THEN -jd.amount
                    ELSE 0
                END
            ), 0
        )
        FROM journal_detail jd
        JOIN journal_header jh
            ON jh.id = jd.journal_header_id
        WHERE jd.gl_account_id = :glAccountId
        AND jh.status = 'POSTED'
        AND jh.cabang_id = :cabangId
        AND jh.transaction_date < :akhirPeriode
        """, nativeQuery = true)
    BigDecimal findSaldoCadanganKerugian(
        @Param("glAccountId") Integer glAccountId,
        @Param("cabangId") Integer cabangId,
        @Param("akhirPeriode") LocalDateTime akhirPeriode
    );

    


}