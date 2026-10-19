package com.project.gl.service;

import com.project.gl.model.response.NomorGelAccountResponse;
import com.project.gl.model.entity.NomorGlAccount;
import com.project.gl.model.request.NomorGlAccountRequest;
import com.project.gl.model.response.PageResponse;

import java.util.List;

public interface NomorGlAccountService {
    
    List<NomorGelAccountResponse> getAll();
    
    NomorGelAccountResponse getById(Integer id);
    
    NomorGelAccountResponse getByNomorGl(String nomorGl);
    
    NomorGelAccountResponse create(NomorGlAccountRequest request);
    
    NomorGelAccountResponse update(Integer id, NomorGlAccountRequest request);
    
    void delete(Integer id);
    
    PageResponse<NomorGelAccountResponse> getAllWithFilter(
            int page,
            int size,
            String sortBy,
            String orderDir,
            String nomorGl,
            String namaGlAccount,
            Integer typeId,
            String saldoNormal
    );

    List<NomorGelAccountResponse>  getAllWithFilterExport(
            String ids,
            String nomorGl,
            String namaGlAccount,
            Integer typeId,
            String saldoNormal);
}
