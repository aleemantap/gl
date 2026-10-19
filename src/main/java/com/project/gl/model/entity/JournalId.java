package com.project.gl.model.entity;

import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
@Entity
@Table(
    name = "journal_id",
    uniqueConstraints = {
        @UniqueConstraint(name = "journal_code", columnNames = "journal_code")
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalId extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "journal_code", nullable = false, length = 20, unique = true)
    private String journalCode;


    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

  

    // Otomatis mengisi waktu saat data baru pertama kali disimpan
    @PrePersist
    protected void onCreate() {
        // if (this.createdAt == null) {
        //     this.createdAt = LocalDateTime.now();
        // }
        // if (this.updatedAt == null) {
        //     this.updatedAt = LocalDateTime.now();
        // }
        if (this.isActive == null) {
            this.isActive = true;
        }
       
    }

    // Otomatis memperbarui waktu saat data diperbarui
    // @PreUpdate
    // protected void onUpdate() {
    //     this.updatedAt = LocalDateTime.now();
    // }
}
