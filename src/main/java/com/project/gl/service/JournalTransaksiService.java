package com.project.gl.service;

import org.springframework.data.domain.Page;

import com.project.gl.model.entity.JournalId;
import com.project.gl.model.entity.TransactionCodeJournal;
import com.project.gl.model.request.TransactionJournalRequest;
import com.project.gl.model.response.JournaTransactionResponse;
import com.project.gl.model.response.JournalIDResponse;
import com.project.gl.model.response.TransactionJournalSimpleResponse;

public interface JournalTransaksiService {
    Page<JournaTransactionResponse> getAllWithFilter(
            int page, 
            int size,
            String cabangId
    );

    // JournaTransactionResponse create(TransactionJournalRequest request);
    void create(TransactionJournalRequest request);
    TransactionJournalSimpleResponse getById(Long id);   
    void update(Long id, TransactionJournalRequest request);
    void delete(Long id); 
} 