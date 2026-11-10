package com.project.gl.model.response;

import com.project.gl.model.projection.KodeTransaksiComboProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KodeTransaksiComboResponse {

    private Integer id;
    private String kode;
    private String namaTransaksi;
    private Integer cabangId;

    public KodeTransaksiComboResponse(KodeTransaksiComboProjection projection) {

        this.id = projection.getId();
        this.kode = projection.getKode();
        this.namaTransaksi = projection.getNamaTransaksi();
        // this.cabangId = projection.getCabangId();
    }
}