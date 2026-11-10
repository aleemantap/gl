package com.project.gl.service;

import com.project.gl.model.entity.KasPoint;
import com.project.gl.model.entity.Teller;
import com.project.gl.model.projection.KasPointTellerProjection;
import com.project.gl.model.response.KasPointResponse;

import java.util.List;

public interface KasPointService {
    List<KasPointResponse> findAll(String code, String nama, String jenis, Integer cabangId);
    KasPoint findById(Long id);
    KasPoint save(KasPoint kasPoint);
    KasPoint update(Long id, KasPoint kasPoint);
    void delete(Long id);
    List<KasPointTellerProjection> getAll();
}