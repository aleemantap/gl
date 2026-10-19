package com.project.gl.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_code_journal")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCodeJournal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "kode_transaksi_id", nullable = false)
    private Integer kodeTransaksiId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kode_transaksi_id", referencedColumnName = "id", insertable = false, updatable = false)
    private KodeTransaksi kodeTransaksi;

    @Column(name = "cabang_id", nullable = false)
    private Integer cabangId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cabang_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Cabang cabang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_id", referencedColumnName = "id", nullable = false)
    private JournalId journalId;

    @Column(name = "journal_id", insertable = false, updatable = false)
    private Integer journalIdField; // Field untuk akses langsung ke ID

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    // Constructor untuk memudahkan pembuatan object
    public TransactionCodeJournal(Integer kodeTransaksiId, Integer cabangId, JournalId journalId, String namaTransaksiOperasional) {
        this.kodeTransaksiId = kodeTransaksiId;
        this.cabangId = cabangId;
        this.journalId = journalId;
        
    }
}