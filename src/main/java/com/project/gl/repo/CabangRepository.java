package com.project.gl.repo;

import com.project.gl.model.entity.Cabang;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface CabangRepository extends JpaRepository<Cabang, Integer> {
    
   
    Optional<Cabang> findByKodeCabang(String code);

    @NonNull
    Optional<Cabang> findById(Integer id);

     // Find by kodeCabang and namaCabang (exact match) with pagination
    // Page<Cabang> findByKodeCabangAndNamaCabang(String kodeCabang, String namaCabang, Pageable pageable);
    
     @Query("SELECT c FROM Cabang c WHERE " +
           "(:kodeCabang IS NULL OR :kodeCabang = '' OR c.kodeCabang = :kodeCabang) AND " +
           "(:namaCabang IS NULL OR :namaCabang = '' OR c.namaCabang = :namaCabang)")
     Page<Cabang> findByKodeCabangAndNamaCabang(
            @Param("kodeCabang") String kodeCabang, 
            @Param("namaCabang") String namaCabang, 
            Pageable pageable);


      @Query("SELECT c FROM Cabang c WHERE " +
           "(:idsEmpty = true OR c.id IN (:ids)) AND"+
           "(:kodeCabang IS NULL OR :kodeCabang = '' OR c.kodeCabang = :kodeCabang) AND " +
           "(:namaCabang IS NULL OR :namaCabang = '' OR c.namaCabang = :namaCabang)")
      List<Cabang> findByKodeCabangAndNamaCabangExport(
            @Param("ids") List<Long> ids,
            @Param("idsEmpty") boolean idsEmpty,
            @Param("kodeCabang") String kodeCabang, 
            @Param("namaCabang") String namaCabang 
            );
      
   
}