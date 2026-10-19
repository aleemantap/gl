package com.project.gl.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionJournalSimpleResponse {
    private Long id;
    private Integer kodeTransaksiId;
    private Integer cabangId;
    private Long journalId;
}