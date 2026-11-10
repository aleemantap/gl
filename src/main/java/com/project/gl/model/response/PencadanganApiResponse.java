package com.project.gl.model.response;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PencadanganApiResponse {

    private boolean success;
    private String message;
    private Data data;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {

        private String periode;

        @JsonProperty("tanggal_posisi")
        private String tanggalPosisi;

        @JsonProperty("kode_cabang")
        private String kodeCabang;

        @JsonProperty("nama_cabang")
        private String namaCabang;

        @JsonProperty("referensi_laporan")
        private String referensiLaporan;

        @JsonProperty("total_cadangan")
        private BigDecimal totalCadangan;

        private String currency;
    }
}
