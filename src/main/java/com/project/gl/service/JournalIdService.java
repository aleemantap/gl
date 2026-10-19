package com.project.gl.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.project.gl.model.entity.JournalId;
import com.project.gl.model.request.JurnalComponentAddOne;

import com.project.gl.model.request.JournalAddRequest;
import com.project.gl.model.response.JournalIDResponse;
import com.project.gl.model.response.JournalLineTemplateResponse;
import com.project.gl.model.response.PageResponse;

public interface JournalIdService {


    JournalId generateRandomCode(JournalId journalId);    
    // boolean isJournalCodeExists(String journalCode);
    JournalIDResponse create(JournalAddRequest request);
    JournalLineTemplateResponse createOneJlt(JurnalComponentAddOne request);
    JournalIDResponse getJournalIdById(Long id);
    Page<JournalIDResponse> getJournalIdsWithPagination(Pageable pageable);
    List<JournalLineTemplateResponse> getJournalLineTemplate(Long journalId);
    JournalIDResponse updateJournalId(Long id,  String description, Boolean isActive);
    void hapusJournalId(Long id);
//createOneJlt 
    // PageResponse<JournalIDResponse> getAllWithFilter(
    //         int page, 
    //         int size, 
    //         String sortBy, 
    //         String orderDir,
    //         String kode,
    //         String namaTransaksi
    // );

    
   
    // // JournalIDResponse createJournalId(JournalId journalId);
    // JournalIDResponse updateJournalId(Long id, JournalId journalId);
    // // void deleteJournalId(Long id);

     // New method untuk pagination
    
}
