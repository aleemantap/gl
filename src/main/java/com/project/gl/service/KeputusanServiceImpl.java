package com.project.gl.service;

import com.project.gl.model.entity.Keputusan;
import com.project.gl.model.enums.KeputusanStatus;
import com.project.gl.repo.KeputusanRepository;
import com.project.gl.service.KeputusanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class KeputusanServiceImpl implements KeputusanService {

    private final KeputusanRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<Keputusan> findAll() {
        return repository.findAllByOrderByTanggalDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Keputusan findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Keputusan tidak ditemukan dengan ID: " + id
                        )
                );
    }

    @Override
    public Keputusan save(Keputusan keputusan) {

        if (keputusan.getStatus() == null) {
            keputusan.setStatus(KeputusanStatus.TERBUKA);
        }

        return repository.save(keputusan);
    }

    @Override
    public void deleteById(Long id) {

        Keputusan keputusan = findById(id);

        repository.delete(keputusan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Keputusan> findByStatus(
            KeputusanStatus status) {

        return repository.findByStatusOrderByTanggalDesc(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Keputusan> findByKategori(
            String kategori) {

        return repository.findByKategoriOrderByTanggalDesc(kategori);
    }
}