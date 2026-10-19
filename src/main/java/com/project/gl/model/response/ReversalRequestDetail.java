package com.project.gl.model.response;
import java.math.BigDecimal;
import java.util.List;

import com.project.gl.model.entity.JournalDetail;

public record ReversalRequestDetail(
        Long id,
        Long originalJournalId,
        String journalNo,
        String transactionNo,
        String cabangCode,
        String cabangName,
        String requestedBy,
        String requestedAt,
        String reason,
        String description,
        String status,
        BigDecimal totalDebit,
        BigDecimal totalCredit,
        List<JournalDetailRow> journalDetails,
        List<CashMovementRow> cashMovements
) {
}