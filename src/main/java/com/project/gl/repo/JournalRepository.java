package com.project.gl.repo;

import com.project.gl.model.entity.JournalId;
import com.project.gl.model.response.JournalIDResponse;

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


}