package com.project.gl.service;

import com.project.gl.model.response.TellerResponse;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.Teller;

import java.util.List;

public interface TellerService {
    List<TellerResponse> findAll(String nama, String userGadai);
    Teller findById(Long id);
    Teller save(Teller teller);
    Teller update(Long id, Teller teller);
    void delete(Long id);
    List<Teller> getAll();
}