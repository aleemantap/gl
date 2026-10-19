package com.project.gl.repo;

import com.project.gl.model.entity.TransactionCodeJournal;
import com.project.gl.model.request.TransactionJournalRequest;
import com.project.gl.model.response.JournaTransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JournalTransaksiRepository extends JpaRepository<TransactionCodeJournal, Integer> {
       @Query("SELECT new com.project.gl.model.response.JournaTransactionResponse(" +
              "kt.kode, " +                           // String
              "kt.namaTransaksi, " +                  // String
              "c.namaCabang, " +                      // String
              "CAST(jt.journalId.id AS string), " +  // String - journalId
              "jt.journalId.journalCode) " +         // String - journalCode
              "FROM TransactionCodeJournal jt " +
              "JOIN KodeTransaksi kt ON jt.kodeTransaksiId = kt.id " +
              "JOIN Cabang c ON jt.cabangId = c.id " +
              "WHERE (:kode IS NULL OR :kode = '' OR LOWER(kt.kode) LIKE LOWER(CONCAT('%', :kode, '%'))) " +
              "AND (:namaTransaksi IS NULL OR :namaTransaksi = '' OR LOWER(kt.namaTransaksi) LIKE LOWER(CONCAT('%', :namaTransaksi, '%'))) " +
              "AND (:namaCabang IS NULL OR :namaCabang = '' OR LOWER(c.namaCabang) LIKE LOWER(CONCAT('%', :namaCabang, '%'))) " +
              "AND (:journalId IS NULL OR :journalId = '' OR LOWER(CAST(jt.journalId.id AS string)) LIKE LOWER(CONCAT('%', :journalId, '%')))")
       Page<Object[]> findJournalDataWithFilter(
              @Param("kode") String kode,
              @Param("namaTransaksi") String namaTransaksi,
              @Param("namaCabang") String namaCabang,
              @Param("journalId") String journalId,
              Pageable pageable);

//       void update(Long id, TransactionJournalRequest request);

}