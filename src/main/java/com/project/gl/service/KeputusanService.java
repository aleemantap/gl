package com.project.gl.service;

import com.project.gl.model.entity.Keputusan;
import com.project.gl.model.enums.KeputusanStatus;

import java.util.List;

public interface KeputusanService {

    List<Keputusan> findAll();

    Keputusan findById(Long id);

    Keputusan save(Keputusan keputusan);

    void deleteById(Long id);

    List<Keputusan> findByStatus(KeputusanStatus status);

    List<Keputusan> findByKategori(String kategori);
}