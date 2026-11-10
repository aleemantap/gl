package com.project.gl.service;

import com.project.gl.model.entity.KasPoint;
import com.project.gl.model.entity.Teller;
import com.project.gl.model.projection.KasPointTellerProjection;
import com.project.gl.model.response.KasPointResponse;
import com.project.gl.repo.KasPointRepository;
import com.project.gl.service.KasPointService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.sql.Timestamp;


@Service
public class KasPointServiceImpl implements KasPointService {

    private final KasPointRepository repository;

    public KasPointServiceImpl(KasPointRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<KasPointResponse> findAll(String code,String nama, String jenis, Integer cabangId) {
        List<Object[]> rows = repository.findAllWithCabangRaw(code,  nama, jenis, cabangId);
        return rows.stream().map(this::mapRow).toList();
    }

    @Override
    public List<KasPointTellerProjection> getAll() {
        return repository.getAllKasPointWithTeller();
    }

    /** Mapping Object[] -> KasPointResponse */
    private KasPointResponse mapRow(Object[] r) {
        Long id          = r[0] != null ? ((Number) r[0]).longValue() : null;
        String code      = (String) r[1];
        String nama      = (String) r[2];
        String jenis     = (String) r[3];
        Integer cabangId = r[3] != null ? ((Number) r[4]).intValue() : null;
        String kodeCab   = (String) r[5];
        String namaCab   = (String) r[6];
        LocalDateTime createdAt = toLocalDateTime(r[7]);
        LocalDateTime updatedAt = toLocalDateTime(r[8]);
        return new KasPointResponse(id, code, nama, jenis, cabangId, kodeCab, namaCab, createdAt, updatedAt);
    }

    private LocalDateTime toLocalDateTime(Object v) {
        if (v == null) return null;
        if (v instanceof LocalDateTime ldt) return ldt;
        if (v instanceof Timestamp ts) return ts.toLocalDateTime();
        return null;
    }


    @Override
    public KasPoint findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("KasPoint tidak ditemukan: " + id));
    }

    @Override
    public KasPoint save(KasPoint kasPoint) {
        return repository.save(kasPoint);
    }

    @Override
    public KasPoint update(Long id, KasPoint kasPoint) {
        KasPoint existing = findById(id);
        // existing.setK(kasPoint.getNama());
        existing.setNama(kasPoint.getNama());
        existing.setJenis(kasPoint.getJenis());
        existing.setCabangId(kasPoint.getCabangId());
        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}