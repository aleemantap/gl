package com.project.gl.repo;

import com.project.gl.model.entity.ReversalRequest;
import com.project.gl.model.enums.ReversalRequestStatus;
import com.project.gl.model.response.ReversalRequestRow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReversalRequestRepository extends JpaRepository<ReversalRequest, Long> {

    @Query("""
        SELECT new com.project.gl.model.response.ReversalRequestRow(
            r.id,
            r.originalJournalId,
            j.journalNo,
            j.transactionNo,
            j.transactionDate,
            c.kodeCabang,
            c.namaCabang,
            r.requestedBy,
            r.requestedAt,
            r.reason,
            r.description,
            r.status
        )
        FROM ReversalRequest r
        JOIN JournalHeader j
            ON j.id = r.originalJournalId
        LEFT JOIN j.cabang c
        ORDER BY r.requestedAt DESC
    """)
    List<ReversalRequestRow> findAllRows();

    List<ReversalRequest> findAllByOrderByRequestedAtDesc();

    List<ReversalRequest> findByStatusOrderByRequestedAtDesc(
            ReversalRequestStatus status
    );

    boolean existsByOriginalJournalIdAndStatus(
            Long originalJournalId,
            ReversalRequestStatus status
    );
}