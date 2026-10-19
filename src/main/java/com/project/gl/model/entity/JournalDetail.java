package com.project.gl.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "journal_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JournalDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "journal_header_id", nullable = false)
    private Long journalHeaderId;

    

    @Column(name = "gl_account_id", nullable = false)
    private Long glAccountId;

    @Column(name = "dk", nullable = false, length = 1)
    private String dk;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

      
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_header_id", insertable = false, updatable = false)
    private JournalHeader journalHeader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gl_account_id", insertable = false, updatable = false)
    private NomorGlAccount nomorGlAccount;

    
}