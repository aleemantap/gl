package com.project.gl.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class TellerResponse {

    private Long id;
    private String nama;
    private String userGadai;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public TellerResponse() {}

    public TellerResponse(Long id, String nama, String userGadai,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nama = nama;
        this.userGadai = userGadai;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // getter & setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getUserGadai() { return userGadai; }
    public void setUserGadai(String userGadai) { this.userGadai = userGadai; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}