package com.project.gl.repo;

import com.project.gl.model.entity.TransactionCodeJournal;
import com.project.gl.model.request.TransactionJournalRequest;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.project.gl.model.response.JournaTransactionResponse;
import com.project.gl.model.response.TransactionJournalSimpleResponse;



public interface JournalTransactionRepository extends JpaRepository<TransactionCodeJournal, Long> {
   
    @Query("SELECT new com.project.gl.model.response.TransactionJournalSimpleResponse(" +
       "tj.id, " +
       "tj.kodeTransaksiId, " +
       "tj.cabangId, " +
       "tj.journalId.id" +  // Ambil ID dari entity JournalId
       ") " +
       "FROM TransactionCodeJournal tj " +
       "WHERE tj.id = :id")
    Optional<TransactionJournalSimpleResponse> findTransactionJournalById(@Param("id") Long id);    


    @Query("SELECT COUNT(t) FROM TransactionCodeJournal t WHERE t.journalId.id = :journalId")
    Long countByJournalId(@Param("journalId") Long journalId);

    // Mencari semua transaksi berdasarkan journalId
    @Query("SELECT t FROM TransactionCodeJournal t WHERE t.journalId.id = :journalId")
    List<TransactionCodeJournal> findByJournalId(@Param("journalId") Long journalId);

    // Mencari transaksi berdasarkan cabang
    @Query("SELECT t FROM TransactionCodeJournal t WHERE t.cabangId = :cabangId")
    List<TransactionCodeJournal> findByCabangId(@Param("cabangId") Long cabangId);

    // Mencari transaksi berdasarkan kode transaksi
    @Query("SELECT t FROM TransactionCodeJournal t WHERE t.kodeTransaksiId = :kodeTransaksiId")
    List<TransactionCodeJournal> findByKodeTransaksiId(@Param("kodeTransaksiId") Long kodeTransaksiId);

    // Menghitung total transaksi per journalId
    @Query("SELECT t.journalId.id, COUNT(t) FROM TransactionCodeJournal t GROUP BY t.journalId.id")
    List<Object[]> countTransactionsByJournalId();

    
}