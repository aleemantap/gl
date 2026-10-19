package com.project.gl.model.response;
import java.math.BigDecimal;

public record JournalDetailRow(
        Long id,
        Long glAccountId,
        String nomorGl,
        String namaGlAccount,
        String dk,
        BigDecimal amount,
        String description
) {
}