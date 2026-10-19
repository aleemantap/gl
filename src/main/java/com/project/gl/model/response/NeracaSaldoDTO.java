package com.project.gl.model.response;

import java.math.BigDecimal;
public class NeracaSaldoDTO {

    private Long glAccountId;
    private String accountCode;
    private String accountName;
    private String normalBalance;

    private BigDecimal saldoAwal;
    private BigDecimal totalDebit;
    private BigDecimal totalKredit;

    private BigDecimal saldoAkhirDebit;
    private BigDecimal saldoAkhirKredit;

    public NeracaSaldoDTO() {
    }

    public NeracaSaldoDTO(
            Long glAccountId,
            String accountCode,
            String accountName,
            String normalBalance,
            BigDecimal saldoAwal,
            BigDecimal totalDebit,
            BigDecimal totalKredit,
            BigDecimal saldoAkhirDebit,
            BigDecimal saldoAkhirKredit
    ) {
        this.glAccountId = glAccountId;
        this.accountCode = accountCode;
        this.accountName = accountName;
        this.normalBalance = normalBalance;
        this.saldoAwal = saldoAwal;
        this.totalDebit = totalDebit;
        this.totalKredit = totalKredit;
        this.saldoAkhirDebit = saldoAkhirDebit;
        this.saldoAkhirKredit = saldoAkhirKredit;
    }

    public Long getGlAccountId() {
        return glAccountId;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getNormalBalance() {
        return normalBalance;
    }

    public BigDecimal getSaldoAwal() {
        return saldoAwal;
    }

    public BigDecimal getTotalDebit() {
        return totalDebit;
    }

    public BigDecimal getTotalKredit() {
        return totalKredit;
    }

    public BigDecimal getSaldoAkhirDebit() {
        return saldoAkhirDebit;
    }

    public BigDecimal getSaldoAkhirKredit() {
        return saldoAkhirKredit;
    }
}