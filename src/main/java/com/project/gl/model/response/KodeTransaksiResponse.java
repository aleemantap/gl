package com.project.gl.model.response;

import com.project.gl.model.entity.KodeTransaksi;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KodeTransaksiResponse {
    private Integer id;
    private String kode;
    private String namaTransaksi;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public KodeTransaksiResponse(KodeTransaksi kodeTransaksi) {
        this.id = kodeTransaksi.getId();
        this.kode = kodeTransaksi.getKode();
        this.namaTransaksi = kodeTransaksi.getNamaTransaksi();
        this.createdAt = kodeTransaksi.getCreatedAt();
        this.updatedAt = kodeTransaksi.getUpdatedAt();
    }

  
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getNamaTransaksi() {
        return namaTransaksi;
    }

    public void setNamaTransaksi(String namaTransaksi) {
        this.namaTransaksi = namaTransaksi;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}