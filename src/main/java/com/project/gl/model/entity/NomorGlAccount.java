package com.project.gl.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "nomor_gl_account")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NomorGlAccount extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nomor_gl", length = 50, nullable = false)
    private String nomorGl;

    @Column(name = "nama_gl_account", length = 100, nullable = false)
    private String namaGlAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_gl_account_id", referencedColumnName = "id", insertable = false, updatable = false)
    private GlAccountType glAccountType;

    @Column(name = "type_gl_account_id")
    private Integer typeGlAccountId;

    @Column(name = "saldo_normal", length = 10, nullable = false)
    private String saldoNormal;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "cabang_id", referencedColumnName = "id", insertable = false, updatable = false)
    // private Cabang cabang;

    // @Column(name = "cabang_id")
    // private Integer cabangId;

    // @CreationTimestamp
    // @Column(name = "created_at", updatable = false)
    // private LocalDateTime createdAt;

    // @UpdateTimestamp
    // @Column(name = "updated_at")
    // private LocalDateTime updatedAt;
}