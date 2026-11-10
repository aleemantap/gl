package com.project.gl.service;

import com.project.gl.model.response.TellerResponse;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.Teller;
import com.project.gl.repo.TellerRepository;
import com.project.gl.service.TellerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TellerServiceImpl implements TellerService {

    @Autowired 
    private  TellerRepository repository;

    // public TellerServiceImpl(TellerRepository repository) {
    //     this.repository = repository;
    // }

    @Override
    public List<TellerResponse> findAll(String nama, String userGadai) {
        String n = (nama == null) ? "" : nama.trim();
        String u = (userGadai == null) ? "" : userGadai.trim();

        List<Object[]> rows = repository.findAllWithFilter(n, u);
        return rows.stream().map(this::mapRow).toList();
    }

    @Override
    public List<Teller> getAll() {
        return repository.getAll();
    }

    private TellerResponse mapRow(Object[] r) {
        Long id          = r[0] != null ? ((Number) r[0]).longValue() : null;
        String nama      = (String) r[1];
        String userGadai = (String) r[2];
        LocalDateTime createdAt = toLocalDateTime(r[3]);
        LocalDateTime updatedAt = toLocalDateTime(r[4]);
        return new TellerResponse(id, nama, userGadai, createdAt, updatedAt);
    }

    private LocalDateTime toLocalDateTime(Object v) {
        if (v == null) return null;
        if (v instanceof LocalDateTime ldt) return ldt;
        if (v instanceof Timestamp ts) return ts.toLocalDateTime();
        return null;
    }

    @Override
    public Teller findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teller tidak ditemukan: " + id));
    }

    @Override
    @Transactional
    public Teller save(Teller teller) {
        return repository.save(teller);
    }

    @Override
    @Transactional
    public Teller update(Long id, Teller teller) {
        Teller existing = findById(id);
        existing.setNama(teller.getNama());
        existing.setUserGadai(teller.getUserGadai());
        return repository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}