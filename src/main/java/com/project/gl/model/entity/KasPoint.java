package com.project.gl.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "kas_point")
public class KasPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "kas_point_code", nullable = false)
    private String kasPointCode;

    @Column(nullable = false)
    private String nama;

    @Column(nullable = false)
    private String jenis;

    @Column(name = "cabang_id", nullable = false)
    private Long cabangId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cabang_id", insertable = false, updatable = false)
    private Cabang cabang;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // getter & setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }


    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }


    public String getKasPointCode() { return kasPointCode; }
    public void setKasPointCode(String kasPointCode) { this.kasPointCode = kasPointCode; }

    public String getJenis() { return jenis; }
    public void setJenis(String jenis) { this.jenis = jenis; }

    public Long getCabangId() { return cabangId; }
    public void setCabangId(Long cabangId) { this.cabangId = cabangId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}