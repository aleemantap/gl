package com.project.gl.repo;

import com.project.gl.model.entity.JournalDetail;
import com.project.gl.model.entity.JournalHeader;
import com.project.gl.model.response.JournalEntryResponse;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalHeader, Long> {

     @Query("""
        SELECT new com.project.gl.model.response.JournalEntryResponse(
            jh.id,
            jh.journalNo,
            jh.transactionDate,
            jh.transactionNo,
            tc.kode,
            b.kodeCabang,
            ga.id,
            ga.nomorGl,
            ga.namaGlAccount,
            jd.description,
            jd.dk,
            jd.amount,
            jh.status,
            b.namaCabang         
        )
        FROM JournalHeader jh
        JOIN jh.journalDetails jd
        JOIN jh.transactionCode tc
        JOIN jh.cabang b
        JOIN jd.nomorGlAccount ga
        WHERE 
         (:periodeStart IS NULL OR jh.transactionDate >= :periodeStart)
        AND (:periodeEnd IS NULL OR jh.transactionDate < :periodeEnd)
        AND (:branchId IS NULL OR b.id = :branchId)
        AND (:kodeTc IS NULL OR :kodeTc = '' OR LOWER(tc.kode) = LOWER(:kodeTc))
        
      
    """)
    Page<JournalEntryResponse> findJournalEntriesWithFilters(
            @Param("periodeStart") LocalDateTime periodeStart,
            @Param("periodeEnd") LocalDateTime periodeEnd,
            @Param("branchId") Integer branchId,
            @Param("kodeTc") String kodeTc,          
            Pageable pageable
    );
       
    // @Query("SELECT COUNT(jd) FROM JournalDetail jd WHERE jd.journalHeader.id = :headerId")
    // Long countDetailsByHeaderId(@Param("headerId") Long headerId);

   
}
