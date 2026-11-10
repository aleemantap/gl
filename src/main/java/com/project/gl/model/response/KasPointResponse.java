package com.project.gl.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class KasPointResponse {

    private Long id;
    private String kasPointCode;
    private String nama;
    private String jenis;
    private Integer cabangId;
    private String kodeCabang;
    private String namaCabang;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public KasPointResponse() {}

    public KasPointResponse(Long id, String kasPointCode, String nama, String jenis,
                            Integer cabangId, String kodeCabang, String namaCabang,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.kasPointCode = kasPointCode;
        this.nama = nama;
        this.jenis = jenis;
        this.cabangId = cabangId;
        this.kodeCabang = kodeCabang;
        this.namaCabang = namaCabang;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // getter & setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getKasPointCode() { return kasPointCode; }
    public void setKasPointCode(String kasPointCode) { this.kasPointCode = kasPointCode; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getJenis() { return jenis; }
    public void setJenis(String jenis) { this.jenis = jenis; }
    public Integer getCabangId() { return cabangId; }
    public void setCabangId(Integer cabangId) { this.cabangId = cabangId; }
    public String getKodeCabang() { return kodeCabang; }
    public void setKodeCabang(String kodeCabang) { this.kodeCabang = kodeCabang; }
    public String getNamaCabang() { return namaCabang; }
    public void setNamaCabang(String namaCabang) { this.namaCabang = namaCabang; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}