package com.project.gl.service;

import com.project.gl.model.entity.Cabang;
import com.project.gl.model.response.PageResponse;

import java.util.List;

public interface CabangService {
    
    List<Cabang> getAll();
    
    Cabang getById(Integer id);
    
    Cabang create(Cabang request);
    
    Cabang update(Integer id, Cabang request);
    
    void delete(Integer id);
    
    PageResponse<Cabang> getAllWithFilter(
            int page,
            int size,
            String sortBy,
            String orderDir,
            String kode,
            String namaCabang
    );

    List<Cabang> getAllWithFilterExport(
            String listIds,
            String kode,
            String namaCabang
    );

    Cabang getByKodeCabang(String kodeCabang);
}