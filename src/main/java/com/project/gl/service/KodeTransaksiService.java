package com.project.gl.service;


import com.project.gl.model.entity.KodeTransaksi;
import com.project.gl.model.response.KodeTransaksiResponse;
import com.project.gl.model.response.PageResponse;
// import org.springframework.data.domain.Page;

import java.util.List;

public interface KodeTransaksiService {

    
    List<KodeTransaksiResponse> getAll();

    List<KodeTransaksiResponse> getAllWithFilter(String listIds, String kode, String nama);
    
    KodeTransaksiResponse getById(Integer id);
    
    KodeTransaksiResponse getByKode(String kode);
    
    KodeTransaksiResponse create(KodeTransaksi request);
    
    KodeTransaksiResponse update(Integer id, KodeTransaksi request);
    
    void delete(Integer id);
    
    PageResponse<KodeTransaksiResponse> getAllWithFilterDT(
            int page, 
            int size, 
            String sortBy, 
            String orderDir,
            String kode,
            String namaTransaksi
    );
}