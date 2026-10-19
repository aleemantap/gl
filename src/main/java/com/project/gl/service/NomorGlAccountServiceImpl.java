
package com.project.gl.service;

import com.project.gl.helper.Util;
import com.project.gl.model.entity.NomorGlAccount;
import com.project.gl.model.projection.BukuBesarProjection;
import com.project.gl.model.request.NomorGlAccountRequest;
import com.project.gl.model.response.BukuBesarResponse;
import com.project.gl.model.response.KodeTransaksiResponse;
import com.project.gl.model.response.NomorGelAccountResponse;
import com.project.gl.model.response.PageResponse;
import com.project.gl.repo.NomorGlAccountRepository;
import com.project.gl.service.NomorGlAccountService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NomorGlAccountServiceImpl implements NomorGlAccountService {

    @Autowired
    private  NomorGlAccountRepository repository;

    private final Util util;

    @Override
    public List<NomorGelAccountResponse> getAll() {
        return repository.findAll().stream()
                .map(NomorGelAccountResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public NomorGelAccountResponse getById(Integer id) {
        NomorGlAccount nomor = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nomor GL Account not found with id: " + id));
        return new NomorGelAccountResponse(nomor);
    }

    @Override
    public NomorGelAccountResponse getByNomorGl(String nomorGl) {
        NomorGlAccount nomor = repository.findByNomorGl(nomorGl)
                .orElseThrow(() -> new RuntimeException("Nomor GL Account not found with nomorGl: " + nomorGl));
        return new NomorGelAccountResponse(nomor);
    }

    @Override
    @Transactional
    public NomorGelAccountResponse create(NomorGlAccountRequest request) {
        // Validasi duplikat
        if (repository.existsByNomorGl(request.getNomorGl())) {
            throw new RuntimeException("Nomor GL already exists: " + request.getNomorGl());
        }
        
        // Validasi debitKredit
        if (request.getSaldoNormal() == null || 
            (!request.getSaldoNormal().equalsIgnoreCase("D") && 
             !request.getSaldoNormal().equalsIgnoreCase("K"))) {
            throw new RuntimeException("Debit Kredit must be 'DEBIT' or 'KREDIT'");
        }
        
        // NomorGlAccount saved = repository.save(request);
        NomorGlAccount kode = new NomorGlAccount();
        // kode.setCabangId(request.getCabangId());
        kode.setTypeGlAccountId(request.getTypeGlAccountId());
        kode.setNomorGl(request.getNomorGl());
        kode.setNamaGlAccount(request.getNamaGlAccount());
        // kode.getDebetKredit(request.getDebetKredit());
        kode.setSaldoNormal(request.getSaldoNormal());
      
        NomorGlAccount saved = repository.save(kode); 
        return new NomorGelAccountResponse(saved);
    }

    // @Override
    // public NomorGelAccountResponse getById(Integer id) {
    //     // Pake query dengan fetch join biar dapet nama kategori & cabang
    //     NomorGlAccount nomor = repository.findById(id)
    //             .orElseThrow(() -> new RuntimeException("Nomor GL Account not found with id: " + id));
    //     return new NomorGelAccountResponse(nomor);
    // }

    @Override
    public NomorGelAccountResponse update(Integer id, NomorGlAccountRequest request) {
        NomorGlAccount existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nomor GL Account not found with id: " + id));
        
        // Cek duplikat nomor_gl jika diubah
        if (!existing.getNomorGl().equals(request.getNomorGl()) && 
            repository.existsByNomorGl(request.getNomorGl())) {
            throw new RuntimeException("Nomor GL already exists: " + request.getNomorGl());
        }
        
        // Update fields
        existing.setNomorGl(request.getNomorGl());
        existing.setNamaGlAccount(request.getNamaGlAccount());
        
        if (request.getTypeGlAccountId() != null && request.getTypeGlAccountId() > 0) {
            existing.setTypeGlAccountId(request.getTypeGlAccountId());
        }
        
        // if (request.getCabangId() != null && request.getCabangId() > 0) {
        //     existing.setCabangId(request.getCabangId()); 
        // }
        
        if (request.getSaldoNormal() != null) {
            if (!request.getSaldoNormal().equalsIgnoreCase("D") && 
                !request.getSaldoNormal().equalsIgnoreCase("K")) {
                throw new RuntimeException("Debit Kredit must be 'DEBIT' or 'KREDIT'");
            }
            // existing.getDebetKredit(request.getDebetKredit());
            existing.setSaldoNormal(request.getSaldoNormal());
        }
        
        NomorGlAccount updated = repository.save(existing);
        return new NomorGelAccountResponse(updated);
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Nomor GL Account not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public List<NomorGelAccountResponse> getAllWithFilterExport(String listIds, 
        String nomorGl,
        String namaGlAccount, 
        Integer typeId,  
        String saldoNormal) {

        List<Long> ids = util.parseIdsJson(listIds);
        boolean idsEmpty = (ids == null || ids.isEmpty());
        List<Long> safeIds = idsEmpty ? List.of(-1L) : ids; 

        List<NomorGelAccountResponse> rows =  repository.findWithFiltersExport(
                    safeIds, 
                    idsEmpty, 
                    nomorGl,
                    namaGlAccount,
                    typeId,
                    saldoNormal
            );

        List<NomorGelAccountResponse> data =
            rows.stream()
                    .map(p -> new NomorGelAccountResponse(
                            p.getId(),
                            p.getNomorGl(),
                            p.getNamaGlAccount(),
                            p.getGlTypeId(),
                            p.getGlTypeName(),
                            p.getSaldoNormal(),
                            p.getCreatedAt(),
                            p.getUpdatedAt()
                           
                    ))
                    .toList();
        
        return   data;          

    }


    @Override
    public PageResponse<NomorGelAccountResponse> getAllWithFilter(
            int page,
            int size,
            String sortBy,
            String orderDir,
            String nomorGl,
            String namaGlAccount,
            Integer typeId,
            String saldoNormal
    ) {
        Sort.Direction direction = Sort.Direction.fromString(orderDir);
        Pageable pageable = PageRequest.of(page, size, direction, sortBy);
        
      
        // Query dengan FETCH JOIN (otomatis dapet nama kategori & cabang)
        Page<NomorGlAccount> resultPage = repository.findWithFilters(
                nomorGl, namaGlAccount, typeId, saldoNormal, pageable);
        
        // Mapping ke response (nama kategori & cabang sudah ada)
        List<NomorGelAccountResponse> content = resultPage.getContent().stream()
                .map(NomorGelAccountResponse::new)
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                content,
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages(),
                resultPage.isLast()
        );
    }
}