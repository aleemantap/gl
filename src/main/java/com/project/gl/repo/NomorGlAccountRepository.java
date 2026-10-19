package com.project.gl.repo;

import com.project.gl.model.entity.NomorGlAccount;
import com.project.gl.model.response.KodeTransaksiResponse;
import com.project.gl.model.response.NomorGelAccountResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NomorGlAccountRepository extends JpaRepository<NomorGlAccount, Integer> {
    
    Optional<NomorGlAccount> findByNomorGl(String nomorGl);
    
    boolean existsByNomorGl(String nomorGl);

    boolean existsByTypeGlAccountId(Long id);
//     type_gl_account_id
    Page<NomorGlAccount> findByNomorGlContainingIgnoreCase(String nomorGl, Pageable pageable);
    
    Page<NomorGlAccount> findByNamaGlAccountContainingIgnoreCase(String namaGlAccount, Pageable pageable);
    
    Page<NomorGlAccount> findByNomorGlContainingIgnoreCaseAndNamaGlAccountContainingIgnoreCase(
            String nomorGl, String namaGlAccount, Pageable pageable);
    
    // Filter by kategori (Integer)
    Page<NomorGlAccount> findByTypeGlAccountId(Integer kategori, Pageable pageable);
    
    // Filter by cabangId (Integer)
//     Page<NomorGlAccount> findByCabangId(Integer cabangId, Pageable pageable);
    
    // Filter by debitKredit
    Page<NomorGlAccount> findBySaldoNormal(String debitKredit, Pageable pageable);
    
    // Custom query dengan semua filter
   @Query("""
    SELECT n
    FROM NomorGlAccount n
    LEFT JOIN n.glAccountType k
    WHERE (:nomorGl IS NULL OR 
           LOWER(n.nomorGl) LIKE LOWER(CONCAT('%', :nomorGl, '%')))

      AND (:namaGlAccount IS NULL OR 
           LOWER(n.namaGlAccount) LIKE LOWER(CONCAT('%', :namaGlAccount, '%')))
      AND (:typeId IS NULL OR n.typeGlAccountId = :typeId)
      AND (:saldoNormal IS NULL OR n.saldoNormal = :saldoNormal)

    """)
    Page<NomorGlAccount> findWithFilters(
            @Param("nomorGl") String nomorGl,
            @Param("namaGlAccount") String namaGlAccount,
            @Param("typeId") Integer typeId,
            @Param("saldoNormal") String saldoNormal,
            Pageable pageable
    );

    List<NomorGlAccount> findByIsActiveTrueOrderByNomorGlAsc();


     @Query("""
        SELECT n
        FROM NomorGlAccount n
        LEFT JOIN n.glAccountType k
        WHERE 
        (:idsEmpty = true OR n.id IN (:ids))
        AND (:nomorGl IS NULL OR LOWER(n.nomorGl) LIKE LOWER(CONCAT('%', :nomorGl, '%')))

        AND (:namaGlAccount IS NULL OR LOWER(n.namaGlAccount) LIKE LOWER(CONCAT('%', :namaGlAccount, '%')))
        AND (:typeId IS NULL OR n.typeGlAccountId = :typeId)
        AND (:saldoNormal IS NULL OR n.saldoNormal = :saldoNormal)

    """)
    List<NomorGelAccountResponse> findWithFiltersExport(
            @Param("ids") List<Long> ids,
            @Param("idsEmpty") boolean idsEmpty,
            @Param("nomorGl") String nomorGl,
            @Param("namaGlAccount") String namaGlAccount,
            @Param("typeId") Integer typeId,
            @Param("saldoNormal") String saldoNormal
          
    );

//     List<NomorGlAccount> findByCabangId(Integer cabangId);

//     List<NomorGlAccount> findByNomorGl(String nomorGl);


}