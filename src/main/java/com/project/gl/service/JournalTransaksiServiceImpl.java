package com.project.gl.service;


import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.JournalId;
import com.project.gl.model.entity.KodeTransaksi;
import com.project.gl.model.response.JournaTransactionResponse;
import com.project.gl.model.response.JournalIDResponse;
import com.project.gl.model.response.TransactionJournalSimpleResponse;
import com.project.gl.repo.JournalKodeTransaksiRepository;
import com.project.gl.repo.JournalTransactionRepository;
import com.project.gl.repo.JournalRepository;

import jakarta.transaction.Transactional;

import com.project.gl.model.entity.TransactionCodeJournal;
import com.project.gl.model.request.TransactionJournalRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// import com.project.gl.service.JournalTransaksiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class JournalTransaksiServiceImpl implements JournalTransaksiService {

    @Autowired
    private JournalKodeTransaksiRepository journalTransaksiRepository;

    @Autowired
    private JournalTransactionRepository journalTransactionRepository;


    

    @Autowired
    private JournalRepository journalRepository;

    

    @Override
    public Page<JournaTransactionResponse> getAllWithFilter(
            int page, 
            int size,
            String cabangId) {

        Pageable pageable = PageRequest.of(page, size);        
        Page<Object[]> result = journalTransaksiRepository.findJournalDataWithFilter(
             cabangId, pageable);
        
         List<JournaTransactionResponse> responses = result.getContent().stream()
        .map(obj -> {
            JournaTransactionResponse response = new JournaTransactionResponse();
            response.setId(obj[0] != null ? Integer.valueOf(obj[0].toString()) : null);
            response.setKode(obj[1] != null ? String.valueOf(obj[1]) : null);
            response.setNamaTransaksi(obj[2] != null ? String.valueOf(obj[2]) : null);
            response.setNamaCabang(obj[3] != null ? String.valueOf(obj[3]) : null);
            response.setJournalId(obj[4] != null ? String.valueOf(obj[4]) : null);
            response.setJournalCode(obj[5] != null ? String.valueOf(obj[5]) : null);
            response.setKodeCabang(obj[6] != null ? String.valueOf(obj[6]) : null);
            //(obj[5] != null ? String.valueOf(obj[5]) : null);
            return response;
        })
        .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, result.getTotalElements());
    }

    @Override
    @Transactional
    public void create(TransactionJournalRequest request) {
       
         JournalId journalId = journalRepository.findById(request.getJournalId().longValue())
            .orElseThrow(() -> new RuntimeException("Journal ID tidak ditemukan"));
        
        // Cek apakah kombinasi sudah ada
        // boolean exists = journalTransaksiRepository.existsByKodeTransaksiIdAndCabangIdAndJournalId(
        //     request.getKodeTransaksi(),
        //     request.getCabangId(),
        //     journalId
        // );
        
        // if (exists) {
        //     throw new RuntimeException("Data sudah ada! Kombinasi Kode Transaksi, Cabang, dan Journal ID sudah terdaftar.");
        // }

        boolean exists2 = journalTransaksiRepository.existsByKodeTransaksiIdAndCabangId(
            request.getKodeTransaksi(),
            request.getCabangId()
        );

        if (exists2) {
            throw new RuntimeException("Data sudah ada! Kombinasi Kode Transaksi dan Cabang sudah terdaftar.");
        }
        
        
      
        TransactionCodeJournal transactionJournal = TransactionCodeJournal.builder()
            .kodeTransaksiId(request.getKodeTransaksi())
            .cabangId(request.getCabangId())
             .journalId(journalId)
            .createdBy("SYSTEM")
            .build();

        journalTransaksiRepository.save(transactionJournal);
        
    }

   @Override
    public TransactionJournalSimpleResponse getById(Long id) {
        return journalTransactionRepository.findTransactionJournalById(id)
            .orElseThrow(() -> new RuntimeException("Data tidak ditemukan dengan ID: " + id));
    }

    @Override
    @Transactional
    public void update(Long id, TransactionJournalRequest request) {
        //log.info("Updating transaction journal with id: {}", id);
        
        // 1. Cari data yang akan diupdate
        TransactionCodeJournal existingData = journalTransaksiRepository.findById(id.intValue())
            .orElseThrow(() -> new RuntimeException("Data tidak ditemukan dengan ID: " + id));
        
        // 2. Cari JournalId baru
        JournalId journalId = journalRepository.findById(request.getJournalId().longValue())
            .orElseThrow(() -> new RuntimeException("Journal ID tidak ditemukan"));
        
        // 3. Cek duplikat (kecuali data sendiri)
        // boolean exists = journalTransaksiRepository.existsByKodeTransaksiIdAndCabangIdAndJournalIdAndIdNot(
        //     request.getKodeTransaksi(),
        //     request.getCabangId(),
        //     journalId,
        //     id
        // );

         // 3. Cek duplikat (kecuali data sendiri)
        boolean exists = journalTransaksiRepository.existsByKodeTransaksiIdAndCabangIdAndIdNot(
            request.getKodeTransaksi(),
            request.getCabangId(),
            id
        );
        
        // boolean exists = journalTransaksiRepository.existsByKodeTransaksiIdAndCabangIdAndJournalId(
        //     request.getKodeTransaksi(),
        //     request.getCabangId(),
        //     journalId
        // );
        
        if (exists) {
            throw new RuntimeException("Data sudah ada! Kombinasi Kode Transaksi, Cabang, dan Journal ID sudah terdaftar.");
        }
        
        // 4. Update data
        existingData.setKodeTransaksiId(request.getKodeTransaksi());
        existingData.setCabangId(request.getCabangId());
        existingData.setJournalId(journalId);
        existingData.setUpdatedBy("SYSTEM");
        
        // 5. Simpan perubahan
        journalTransaksiRepository.save(existingData);
        // log.info("Transaction journal with id {} updated successfully", id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
       
        // 1. Cek apakah data ada
        TransactionCodeJournal existingData = journalTransaksiRepository.findById(id.intValue())
            .orElseThrow(() -> new RuntimeException("Data tidak ditemukan dengan ID: " + id));
        
        // 2. Hapus data
        journalTransaksiRepository.delete(existingData);
        
       
    }

}