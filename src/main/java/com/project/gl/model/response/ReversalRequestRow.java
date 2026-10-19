package com.project.gl.model.response;
import com.project.gl.model.enums.ReversalRequestStatus;
import java.time.LocalDateTime;
public record ReversalRequestRow(
        Long id,
        Long originalJournalId,
        String journalNo,
        String transactionNo,
        LocalDateTime transactionDate,
        String cabangCode,
        String cabangName,
        String requestedBy,
        LocalDateTime requestedAt,
        String reason,
        String description,
        ReversalRequestStatus status
) {
}