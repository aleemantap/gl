package com.project.gl.service;

import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.GlAccountType;
import com.project.gl.model.response.PageResponse;

import java.util.List;
import java.util.Optional;

public interface GlAccountTypeService {
    
    List<GlAccountType> getAll();
    
    GlAccountType getById(Integer id);
    
    GlAccountType create(GlAccountType request);
    
    GlAccountType update(Integer id, GlAccountType request);
    
    void delete(Integer id);
    
    PageResponse<GlAccountType> getAllWithFilter(
            int page,
            int size,
            String sortBy,
            String orderDir,
            String name
          
    );

    Optional<GlAccountType> getKategoriById(Integer id);
    GlAccountType saveKategori(GlAccountType kategori);
    GlAccountType updateKategori(GlAccountType kategori);
    void deleteKategori(Integer id);
    List<GlAccountType> getAllWithFilterExport(
            String listIds,
            String kategori
    );
    
}