package com.project.gl.repo;
import com.project.gl.model.entity.CategoryTransaction;
import com.project.gl.model.entity.GlAccountType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Repository
public interface KategoriTransaksiRepository extends JpaRepository<CategoryTransaction, Integer> {
    
    Page<CategoryTransaction> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    Page<CategoryTransaction> findByCodeContainingIgnoreCase(String kode, Pageable pageable);
    
    

    Page<CategoryTransaction> findByNameContainingIgnoreCaseAndCodeContainingIgnoreCase(
            String kode, String name, Pageable pageable);
}