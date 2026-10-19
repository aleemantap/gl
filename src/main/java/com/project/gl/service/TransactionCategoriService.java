package com.project.gl.service;

import com.project.gl.model.entity.CategoryTransaction;
import com.project.gl.model.entity.GlAccountType;
import com.project.gl.model.response.PageResponse;

import java.util.List;
import java.util.Optional;

public interface TransactionCategoriService {
    
    List<CategoryTransaction> getAll();
    
    CategoryTransaction getById(Integer id);
    
    CategoryTransaction create(CategoryTransaction request);
    
    CategoryTransaction update(Integer id, CategoryTransaction request);
    
    void delete(Integer id);
    
    PageResponse<CategoryTransaction> getAllWithFilter(
            int page,
            int size,
            String sortBy,
            String orderDir,
            String kode,
            String name
          
    );

    Optional<CategoryTransaction> getKategoriById(Integer id);
    CategoryTransaction saveKategori(CategoryTransaction kategori);
    CategoryTransaction updateKategori(CategoryTransaction kategori);
    void deleteKategori(Integer id);
}