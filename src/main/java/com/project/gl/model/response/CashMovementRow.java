package com.project.gl.model.response;

import java.math.BigDecimal;

public record CashMovementRow(
        Long id,
        String direction,
        BigDecimal amount,
        String kasPointCode,
        String counterpartyName,
        String description
) {}