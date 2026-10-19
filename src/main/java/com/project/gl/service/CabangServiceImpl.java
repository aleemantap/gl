package com.project.gl.service;

import com.project.gl.helper.Util;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.response.JournalEntryResponse;
import com.project.gl.model.response.PageResponse;
import com.project.gl.repo.CabangRepository;
import com.project.gl.service.CabangService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CabangServiceImpl implements CabangService {

    private final CabangRepository repository;
    private final Util util;

    @Override
    public List<Cabang> getAll() {
        return repository.findAll();
    }

    @Override
    public Cabang getById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cabang not found with id: " + id));
    }

    @Override
    public Cabang create(Cabang request) {
        // Set default status jika tidak diisi
        return repository.save(request);
    }

    @Transactional
    public Cabang update(Integer id, Cabang request) {
        // Cari data existing
        Cabang existingCabang = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cabang dengan ID " + id + " tidak ditemukan"));
        // Update field yang diizinkan
        existingCabang.setKodeCabang(request.getKodeCabang());
        existingCabang.setNamaCabang(request.getNamaCabang());
        existingCabang.setAlamatLengkap(request.getAlamatLengkap());
        // Simpan perubahan
        return repository.save(existingCabang);
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Cabang not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public PageResponse<Cabang> getAllWithFilter(
            int page,
            int size,
            String sortBy,
            String orderDir,
            String kode,
            String namaCabang
    ) {
        // boolean hasName = StringUtils.hasText(namaCabang);
        // boolean hasKode = StringUtils.hasText(kode);
        Sort.Direction direction = Sort.Direction.fromString(orderDir);
        Pageable pageable = PageRequest.of(page, size, direction, sortBy);
        Page<Cabang> resultPage = repository
                .findByKodeCabangAndNamaCabang(
                        kode,
                        namaCabang,                      
                        pageable
                );

        return new PageResponse<>(
                resultPage.getContent(),
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages(),
                resultPage.isLast()
        );
    }

    @Override
    public Cabang getByKodeCabang(String kodeCabang) {

        return repository.findByKodeCabang(kodeCabang)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Cabang tidak ditemukan dengan kode: "
                                        + kodeCabang
                        )
                );
    }

    @Override
    public List<Cabang> getAllWithFilterExport(String idsJson, String kode,String namaCabang) {

        List<Long> ids = util.parseIdsJson(idsJson);

        boolean idsEmpty = (ids == null || ids.isEmpty());
        List<Long> safeIds = idsEmpty ? List.of(-1L) : ids; 

        List<Cabang> rows =  repository.findByKodeCabangAndNamaCabangExport(
                safeIds, 
                idsEmpty, 
                kode,
                namaCabang
        );

        return rows;

        // return rows.stream()
        //     .map(p -> JournalListDTO.builder()
        //             .id(p.getId())
        //             .journalNo(p.getJournalNo())
        //             .transactionNo(p.getTransactionNo())
        //             .transactionCode(p.getTransactionCode())
        //             .branchCode(p.getBranchCode())
        //             .namaCabang(p.getNamaCabang())
        //             .transactionDate(p.getTransactionDate())
        //             .totalDebit(p.getTotalDebit())
        //             .totalCredit(p.getTotalCredit())
        //             .status(p.getStatus())
        //             .reversalStatus(p.getReversalStatus())
        //             .build())
        //     .collect(Collectors.toList());
       
    }

   

    
}