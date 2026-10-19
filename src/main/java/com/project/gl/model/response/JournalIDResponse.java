package com.project.gl.model.response;

import java.time.LocalDateTime;

import com.project.gl.model.entity.JournalId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JournalIDResponse {

    private Long id;
    private String journalCode;
    private String description;
    private Long totalAkun;    
    private Boolean isActive;   
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    // Constructor khusus untuk query 6 field
    public JournalIDResponse(
            Long id,
            String journalCode,
            String description,
            Boolean isActive,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.journalCode = journalCode;
        this.description = description;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public JournalIDResponse(
            Long id,
            String journalCode,
            String description,
            Boolean isActive,
            Long totalAkun,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.journalCode = journalCode;
        this.description = description;
        this.isActive = isActive;
        this.totalAkun = totalAkun;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}

