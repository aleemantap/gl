package com.project.gl.service;


import com.project.gl.model.response.KodeTransaksiResponse;
import com.project.gl.model.response.PageResponse;
import com.project.gl.helper.Util;
import com.project.gl.model.entity.KodeTransaksi;
import com.project.gl.repo.KodeTransaksiRepository;
import com.project.gl.service.KodeTransaksiService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class KodeTransaksiServiceImpl implements KodeTransaksiService {

    private final KodeTransaksiRepository repository;
    private final Util util;

    @Override
    public List<KodeTransaksiResponse> getAll() {
        return repository.findAll().stream()
                .map(KodeTransaksiResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<KodeTransaksiResponse> getAllWithFilter(String listIds, String kode, String nama) {

        List<Long> ids = util.parseIdsJson(listIds);
        boolean idsEmpty = (ids == null || ids.isEmpty());
        List<Long> safeIds = idsEmpty ? List.of(-1L) : ids; 

        return repository.findAllWithFilter(
            safeIds, 
            idsEmpty,
            kode,
            nama
        ).stream()
                .map(KodeTransaksiResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public KodeTransaksiResponse getById(Integer id) {
        KodeTransaksi kodeTransaksi = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kode transaksi not found with id: " + id));
        return new KodeTransaksiResponse(kodeTransaksi);
    }

    @Override
    public KodeTransaksiResponse getByKode(String kode) {
        KodeTransaksi kodeTransaksi = repository.findByKode(kode)
                .orElseThrow(() -> new RuntimeException("Kode transaksi not found with kode: " + kode));
        return new KodeTransaksiResponse(kodeTransaksi);
    }

    @Override
    public KodeTransaksiResponse create(KodeTransaksi request) {
        // Check if kode already exists
        if (repository.existsByKode(request.getKode())) {
            throw new RuntimeException("Kode transaksi already exists: " + request.getKode());
        }
        
        KodeTransaksi saved = repository.save(request);
        return new KodeTransaksiResponse(saved);
    }

    @Override
    public KodeTransaksiResponse update(Integer id, KodeTransaksi request) {
        KodeTransaksi existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kode transaksi not found with id: " + id));
        
        // Check if kode is changed and already exists
        if (!existing.getKode().equals(request.getKode()) && 
            repository.existsByKode(request.getKode())) {
            throw new RuntimeException("Kode transaksi already exists: " + request.getKode());
        }
        
        existing.setKode(request.getKode());
        existing.setNamaTransaksi(request.getNamaTransaksi());
        
        KodeTransaksi updated = repository.save(existing);
        return new KodeTransaksiResponse(updated);
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Kode transaksi not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public PageResponse<KodeTransaksiResponse> getAllWithFilterDT(
            int page, 
            int size, 
            String sortBy, 
            String orderDir,
            String kode,
            String namaTransaksi
    ) {
       
        Sort.Direction direction = Sort.Direction.fromString(orderDir);
        
        Pageable pageable = PageRequest.of(page, size, direction, sortBy);        
        Page<KodeTransaksi> resultPage;
        
        if (StringUtils.hasText(kode) && StringUtils.hasText(namaTransaksi)) {
            resultPage = repository.findByKodeContainingIgnoreCaseAndNamaTransaksiContainingIgnoreCase(
                    kode, namaTransaksi, pageable);
        } else if (StringUtils.hasText(kode)) {
            resultPage = repository.findByKodeContainingIgnoreCase(kode, pageable);
        } else if (StringUtils.hasText(namaTransaksi)) {
            resultPage = repository.findByNamaTransaksiContainingIgnoreCase(namaTransaksi, pageable);
        } else {
            resultPage = repository.findAll(pageable);
        }
        List<KodeTransaksiResponse> content = resultPage.getContent().stream()
                .map(KodeTransaksiResponse::new)
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