package com.project.gl.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class BukuBesarResponse {

    private Long detailId;
    private Long journalHeaderId;
    private LocalDateTime transactionDate;
    private String journalNo;
    private String transactionNo;
    private String transactionCode;
    private String branchCode;
    private String branchName;
    private Long glAccountId;
    private String accountCode;
    private String accountName;
    private String normalBalance;
    private String dk;
    private BigDecimal amount;
    private String description;
    private BigDecimal saldoBerjalan;

    // Constructor dengan semua parameter (kecuali saldoBerjalan)
    public BukuBesarResponse(Long detailId, Long journalHeaderId, LocalDateTime transactionDate,
                             String journalNo, String transactionNo, String transactionCode,
                             String branchCode,
                             String branchName,
                             Long glAccountId, String accountCode,
                             String accountName, String normalBalance, String dk,
                             BigDecimal amount, String description) {
        this.detailId = detailId;
        this.journalHeaderId = journalHeaderId;
        this.transactionDate = transactionDate;
        this.journalNo = journalNo;
        this.transactionNo = transactionNo;
        this.transactionCode = transactionCode;
        this.branchCode = branchCode;
        this.branchName = branchName;
        this.glAccountId = glAccountId;
        this.accountCode = accountCode;
        this.accountName = accountName;
        this.normalBalance = normalBalance;
        this.dk = dk;
        this.amount = amount;
        this.description = description;
        this.saldoBerjalan = BigDecimal.ZERO; // Default 0
    }

    // Constructor lengkap dengan saldoBerjalan
    public BukuBesarResponse(Long detailId, Long journalHeaderId, LocalDateTime transactionDate,
                             String journalNo, String transactionNo, String transactionCode,
                             String branchCode, Long glAccountId, String accountCode,
                             String accountName, String normalBalance, String dk,
                             BigDecimal amount, String description, BigDecimal saldoBerjalan) {
        this.detailId = detailId;
        this.journalHeaderId = journalHeaderId;
        this.transactionDate = transactionDate;
        this.journalNo = journalNo;
        this.transactionNo = transactionNo;
        this.transactionCode = transactionCode;
        this.branchCode = branchCode;
        this.glAccountId = glAccountId;
        this.accountCode = accountCode;
        this.accountName = accountName;
        this.normalBalance = normalBalance;
        this.dk = dk;
        this.amount = amount;
        this.description = description;
        this.saldoBerjalan = saldoBerjalan;
    }

    // Getter dan Setter
    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    public Long getJournalHeaderId() {
        return journalHeaderId;
    }

    public void setJournalHeaderId(Long journalHeaderId) {
        this.journalHeaderId = journalHeaderId;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getJournalNo() {
        return journalNo;
    }

    public void setJournalNo(String journalNo) {
        this.journalNo = journalNo;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Long getGlAccountId() {
        return glAccountId;
    }

    public void setGlAccountId(Long glAccountId) {
        this.glAccountId = glAccountId;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getNormalBalance() {
        return normalBalance;
    }

    public void setNormalBalance(String normalBalance) {
        this.normalBalance = normalBalance;
    }

    public String getDk() {
        return dk;
    }

    public void setDk(String dk) {
        this.dk = dk;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getSaldoBerjalan() {
        return saldoBerjalan;
    }

    public void setSaldoBerjalan(BigDecimal saldoBerjalan) {
        this.saldoBerjalan = saldoBerjalan;
    }
}