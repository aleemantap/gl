package com.project.gl.repo;

import com.project.gl.model.entity.JournalId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JournalIdRepository extends JpaRepository<JournalId, Long> {

    // Optional<JournalId> findByJournalCode(String journalCode);
    // Optional<JournalId> findByJournalCodeAndIsActiveTrue(String journalCode);
}