package com.project.gl.repo;

import com.project.gl.model.entity.TransactionCodeJournal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionJournalRepository
        extends JpaRepository<TransactionCodeJournal, Long> {

    // Optional<TransactionJournal> findByTransactionCodeIdAndBranchId(
    //         Long transactionCodeId,
    //         Long branchId
    // );

    Optional<TransactionCodeJournal> findByKodeTransaksiIdAndCabangId(
            Integer kodeTransaksiId,
            Integer cabangId
    );

   
}