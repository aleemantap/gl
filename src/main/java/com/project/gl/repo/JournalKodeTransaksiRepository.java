package com.project.gl.repo;

import com.project.gl.model.entity.JournalId;
import com.project.gl.model.entity.TransactionCodeJournal;
import com.project.gl.model.projection.CkpnJournalMappingProjection;
import com.project.gl.model.response.JournaTransactionResponse;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalKodeTransaksiRepository extends JpaRepository<TransactionCodeJournal, Integer> {

    
    @Query(value = 
        "SELECT " +
        "   jt.id, " +
        "   kt.kode, " +
        "   kt.nama_transaksi, " +
        "   c.nama_cabang, " +
        "   jt.journal_id, " +
        "   j.journal_code, " +
        "   c.kode_cabang " +
        "FROM transaction_code_journal jt " +
        "INNER JOIN kode_transaksi kt ON jt.kode_transaksi_id = kt.id " +
        "INNER JOIN cabang c ON jt.cabang_id = c.id " +
        "INNER JOIN journal_id j ON jt.journal_id = j.id " +
        "WHERE (:cabangId IS NULL OR :cabangId = '' OR c.id = :cabangId) " +
        "ORDER BY jt.id DESC",
        countQuery = 
        "SELECT COUNT(*) " +
        "FROM transaction_code_journal jt " +
        "INNER JOIN kode_transaksi kt ON jt.kode_transaksi_id = kt.id " +
        "INNER JOIN cabang c ON jt.cabang_id = c.id " +
        "WHERE (:cabangId IS NULL OR :cabangId = '' OR c.id = :cabangId)",
        nativeQuery = true
    )
    Page<Object[]> findJournalDataWithFilter(
        @Param("cabangId") String cabangId,
        Pageable pageable
    );

    boolean existsByKodeTransaksiIdAndCabangIdAndJournalId(
        Integer kodeTransaksiId, 
        Integer cabangId, 
        JournalId journalId
    );    

    boolean existsByKodeTransaksiIdAndCabangId(
        Integer kodeTransaksiId, 
        Integer cabangId
    );  

   boolean existsByKodeTransaksiIdAndCabangIdAndIdNot(
        Integer kodeTransaksiId, 
        Integer cabangId,
        Long id
    );

    boolean existsByKodeTransaksiId(Long kodeTransaksiId);

    @Query(value = """
        SELECT
            tcj.journal_id AS journalId,
            tcj.kode_transaksi_id AS kodeTransaksiId
        FROM transaction_code_journal tcj
        JOIN kode_transaksi kt
            ON kt.id = tcj.kode_transaksi_id
        WHERE kt.kode = 'TX-CKPN'
        AND tcj.cabang_id = :cabangId
        LIMIT 1
        """, nativeQuery = true)
    Optional<CkpnJournalMappingProjection> findCkpnMapping(
        @Param("cabangId") Integer cabangId
    );
}