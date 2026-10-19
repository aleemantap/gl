package com.project.gl.service;

import com.project.gl.model.projection.JournalListProjection;
import com.project.gl.model.response.JournalDetailResponse;
import com.project.gl.model.response.JournalListDTO;
import com.project.gl.model.response.PageResponse;

import java.time.LocalDate;
import java.util.List;

public interface JournalReportService {

    PageResponse<JournalListDTO> findJournalList(
            int page,
            int size,
            String sortBy,
            String orderDir,
            LocalDate fromDate,
            LocalDate toDate,
            String branchCode,
            String transactionCode,
            String journalNo,
            String transactionNo,
            String status,
            String statusReversal
    );

    JournalDetailResponse getJournalDetail(String journalNo);

    // Method KHUSUS Export (Tanpa PageResponse / Pagination)
    List<JournalListDTO> findJournalListForExport(
            String idsJson,
            String fromDate,
            String toDate,
            String branchCode,
            String transactionCode,
            String journalNo,
            String transactionNo,
            String status,
            String statusReversal
    );

}