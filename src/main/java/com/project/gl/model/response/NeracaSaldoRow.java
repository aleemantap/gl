package com.project.gl.model.response;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NeracaSaldoRow {

    private Integer glAccountId;

    private String accountCode;

    private String accountName;

    private BigDecimal debit;

    private BigDecimal credit;
}