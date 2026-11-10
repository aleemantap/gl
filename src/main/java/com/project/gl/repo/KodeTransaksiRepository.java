package com.project.gl.repo;


import com.project.gl.model.entity.KodeTransaksi;
import com.project.gl.model.projection.KodeTransaksiComboProjection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KodeTransaksiRepository extends JpaRepository<KodeTransaksi, Integer> {
    
    Optional<KodeTransaksi> findByKode(String kode);
    
    boolean existsByKode(String kode);

//     boolean existsByNomorGl(String nomorGl);

    // boolean existsByCategoryId(Long id);
    boolean existsByCategoryId(Long categoryId);
    
    // Filter by kode (contains, ignore case)
    Page<KodeTransaksi> findByKodeContainingIgnoreCase(String kode, Pageable pageable);
    
    // Filter by namaTransaksi (contains, ignore case)
    Page<KodeTransaksi> findByNamaTransaksiContainingIgnoreCase(String namaTransaksi, Pageable pageable);
    
    // Filter by both kode and namaTransaksi
    Page<KodeTransaksi> findByKodeContainingIgnoreCaseAndNamaTransaksiContainingIgnoreCase(
            String kode, String namaTransaksi, Pageable pageable);
    
    // Custom query with dynamic filters
    @Query("SELECT k FROM KodeTransaksi k WHERE " +
           "(:kode IS NULL OR LOWER(k.kode) LIKE LOWER(CONCAT('%', :kode, '%'))) AND " +
           "(:namaTransaksi IS NULL OR LOWER(k.namaTransaksi) LIKE LOWER(CONCAT('%', :namaTransaksi, '%')))")
    Page<KodeTransaksi> findWithFilters(
            @Param("kode") String kode, 
            @Param("namaTransaksi") String namaTransaksi, 
            Pageable pageable
    );

     @Query("SELECT k FROM KodeTransaksi k WHERE " +
           "(:idsEmpty = true OR  k.id  IN (:ids)) AND" +
           "(:kode IS NULL OR LOWER(k.kode) LIKE LOWER(CONCAT('%', :kode, '%'))) AND " +
           "(:nama IS NULL OR LOWER(k.namaTransaksi) LIKE LOWER(CONCAT('%', :nama, '%')))")
    List<KodeTransaksi> findAllWithFilter(
            @Param("ids") List<Long> ids,
            @Param("idsEmpty") boolean idsEmpty,
            @Param("kode") String kode, 
            @Param("nama") String nama
    );

  

//     @Query(value = """
//         SELECT
//                 kt.id AS id,
//                 kt.kode AS kode,
//                 kt.nama_transaksi AS namaTransaksi,
//                 tcj.cabang_id AS cabangId
//         FROM transaction_code_journal tcj
//         JOIN kode_transaksi kt
//                 ON kt.id = tcj.kode_transaksi_id
//         WHERE
//         kt.tipe_transaksi = :tipeTransaksi
//         AND kt.tipe_transaksi IS NOT NULL
//         AND EXISTS (
//                 SELECT 1
//                 FROM journal_line_template jlt
//                 WHERE jlt.journal_id = tcj.journal_id
//                 AND jlt.is_active = 1
//                 AND jlt.cash_movement_type = :arahKas
//         )
//         ORDER BY kt.kode
//         """, nativeQuery = true)
//         List<KodeTransaksiComboProjection> findJenisTransaksiCombo(
//                 @Param("tipeTransaksi") String tipeTransaksi,
//                 @Param("arahKas") String arahKas
//                 // @Param("cabangId") Integer cabangId
//         );
    

       @Query(value = """
        SELECT DISTINCT
                kt.id AS id,
                kt.kode AS kode,
                kt.nama_transaksi AS namaTransaksi
        FROM transaction_code_journal tcj
        JOIN kode_transaksi kt
                ON kt.id = tcj.kode_transaksi_id
        WHERE kt.tipe_transaksi = :tipeTransaksi
        AND kt.tipe_transaksi IS NOT NULL
        AND EXISTS (
                SELECT 1
                FROM journal_line_template jlt
                WHERE jlt.journal_id = tcj.journal_id
                AND jlt.is_active = 1
                AND jlt.cash_movement_type = :arahKas
        )
        ORDER BY kt.kode
        """, nativeQuery = true)
        List<KodeTransaksiComboProjection> findJenisTransaksiCombo(
                @Param("tipeTransaksi") String tipeTransaksi,
                @Param("arahKas") String arahKas
        );
}