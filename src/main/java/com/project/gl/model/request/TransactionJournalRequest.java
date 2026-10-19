package com.project.gl.model.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionJournalRequest {
    private Integer kodeTransaksi;
    private Integer cabangId;
    private Integer journalId;
}