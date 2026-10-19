package com.project.gl.model.response;

import com.project.gl.model.entity.NomorGlAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NomorGelAccountResponse {
    private Integer id;
    private String nomorGl;
    private String namaGlAccount;
    private Integer glTypeId;
    private String glTypeName;
    private String saldoNormal;
    // private Integer cabangId;
    // private String cabangName;
    // private String kodeCabang;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public NomorGelAccountResponse(NomorGlAccount nomor) {
        this.id = nomor.getId();
        this.nomorGl = nomor.getNomorGl();
        this.namaGlAccount = nomor.getNamaGlAccount();
        this.saldoNormal = nomor.getSaldoNormal();
        this.createdAt = nomor.getCreatedAt();
        this.updatedAt = nomor.getUpdatedAt();
        
        // Ambil dari relasi (sudah di-fetch join)
        if (nomor.getGlAccountType() != null) {
            this.glTypeId = nomor.getGlAccountType().getId();
            this.glTypeName = nomor.getGlAccountType().getName();
        }
        
        // if (nomor.getCabang() != null) {
        //     this.cabangId = nomor.getCabang().getId();
        //     this.cabangName = nomor.getCabang().getNamaCabang();
        //     this.kodeCabang = nomor.getCabang().getKodeCabang();
        // }
    }
}