package com.project.gl.model.response;

// import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
// import com.project.gl.model.entity.JournalId;

@Data
@NoArgsConstructor
public class JournaTransactionResponse {
    
    private Integer id;
    private String kode;
    private Integer kodeTransaksiId;
    private String namaTransaksi;
    private String namaCabang;
    private Integer cabangId;
    private String journalId;
    private String journalCode;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private String kodeCabang;

    // Constructor untuk 4 parameter (tanpa journalCode, createdBy, createdAt, updatedBy, updatedAt)
    public JournaTransactionResponse(String kode, String namaTransaksi, 
                                    String namaCabang, String journalId) {
        this.kode = kode;
        this.namaTransaksi = namaTransaksi;
        this.namaCabang = namaCabang;
        this.journalId = journalId;
        this.journalCode = null;
        this.createdBy = null;
        this.createdAt = null;
        this.updatedBy = null;
        this.updatedAt = null;
    }

    // Constructor untuk 5 parameter (dengan journalCode, tanpa audit fields)
    public JournaTransactionResponse(String kode, String namaTransaksi, 
                                    String namaCabang, String journalId, 
                                    String journalCode) {
        this.kode = kode;
        this.namaTransaksi = namaTransaksi;
        this.namaCabang = namaCabang;
        this.journalId = journalId;
        this.journalCode = journalCode;
        this.createdBy = null;
        this.createdAt = null;
        this.updatedBy = null;
        this.updatedAt = null;
    }

    // Constructor untuk 8 parameter (lengkap dengan semua field)
    public JournaTransactionResponse(String kode, String namaTransaksi, String namaCabang, 
                                    String journalId, String journalCode,
                                    String createdBy, LocalDateTime createdAt, 
                                    String updatedBy, LocalDateTime updatedAt) {
        this.kode = kode;
        this.namaTransaksi = namaTransaksi;
        this.namaCabang = namaCabang;
        this.journalId = journalId;
        this.journalCode = journalCode;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedBy = updatedBy;
        this.updatedAt = updatedAt;
    }
    
    public JournaTransactionResponse(Integer id, String kode, String namaTransaksi, 
                                    String namaCabang, String journalId, 
                                    String journalCode, 
                                    String kodeCabang) {
        this.id = id;
        this.kode = kode;
        this.namaTransaksi = namaTransaksi;
        this.namaCabang = namaCabang;
        this.journalId = journalId;
        this.journalCode = journalCode;
        this.kodeCabang = kodeCabang;
        
    }
}



//   response.setId(obj[0] != null ? Integer.valueOf(obj[0].toString()) : null);
//             response.setKode(obj[1] != null ? String.valueOf(obj[1]) : null);
//             response.setNamaTransaksi(obj[2] != null ? String.valueOf(obj[2]) : null);
//             response.setNamaCabang(obj[3] != null ? String.valueOf(obj[3]) : null);
//             response.setJournalId(obj[4] != null ? String.valueOf(obj[4]) : null);
//             response.setJournalCode(obj[5] != null ? String.valueOf(obj[5]) : null);
//             response.setKodeCabang(obj[6] != null ? String.valueOf(obj[6]) : null);