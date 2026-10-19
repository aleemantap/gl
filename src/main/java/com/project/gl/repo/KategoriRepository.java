package com.project.gl.repo;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.GlAccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Repository
public interface KategoriRepository extends JpaRepository<GlAccountType, Integer> {
    
    Page<GlAccountType> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    Page<GlAccountType> findByNoteContainingIgnoreCase(String note, Pageable pageable);
    
    Page<GlAccountType> findByNameContainingIgnoreCaseAndNoteContainingIgnoreCase(
            String name, String note, Pageable pageable);

    @Query("SELECT c FROM GlAccountType c WHERE " +
        "(:idsEmpty = true OR c.id IN (:ids)) AND"+
        "(:kategori IS NULL OR :kategori = '' OR c.name = :kategori)")
    List<GlAccountType> findByAllExport(
        @Param("ids") List<Long> ids,
        @Param("idsEmpty") boolean idsEmpty,
        @Param("kategori") String kategori
        );
}