package com.project.gl.model.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.*;


@Entity
@Table(name = "journal_header")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JournalHeader extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "journal_no", nullable = true)
    private String journalNo;

    @Column(name = "journal_id", nullable = false)
    private Long journalId;

    @Column(name = "transaction_code_id", nullable = false)
    private Long transactionCodeId;

    @Column(name = "cabang_id", nullable = false)
    private Long cabangId;

    @Column(name = "transaction_no")
    private String transactionNo;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 100)
    private String idempotencyKey;

    //@Column(name = "created_at")
    //private LocalDateTime createdAt;

    // Relasi ke entity lain (opsional, untuk JOIN di JPQL)
    //@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "kode", insertable = false, updatable = false)
    //private KodeTransaksi transactionCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_code_id", insertable = false, updatable = false)
    private KodeTransaksi transactionCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cabang_id", insertable = false, updatable = false)
    private Cabang cabang;

    @OneToMany(mappedBy = "journalHeader", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<JournalDetail> journalDetails;

    @Column(name = "reversal_of_journal_id")
    private Long reversalOfJournalId;
}