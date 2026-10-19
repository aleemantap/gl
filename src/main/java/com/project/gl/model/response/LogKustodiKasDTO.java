package com.project.gl.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LogKustodiKasDTO {

    private Long id;
    private Long journalHeaderId;

    private LocalDateTime transactionDate;

    private String journalNo;
    private String transactionNo;

    private String transactionCode;
    private String branchCode;

    private String direction;
    private BigDecimal amount;

    private Long tellerUserId;
    private String kasPointCode;

    private String counterpartyType;
    private String counterpartyRef;
    private String counterpartyName;

    private String description;
    private LocalDateTime occurredAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getTellerUserId() {
        return tellerUserId;
    }

    public void setTellerUserId(Long tellerUserId) {
        this.tellerUserId = tellerUserId;
    }

    public String getKasPointCode() {
        return kasPointCode;
    }

    public void setKasPointCode(String kasPointCode) {
        this.kasPointCode = kasPointCode;
    }

    public String getCounterpartyType() {
        return counterpartyType;
    }

    public void setCounterpartyType(String counterpartyType) {
        this.counterpartyType = counterpartyType;
    }

    public String getCounterpartyRef() {
        return counterpartyRef;
    }

    public void setCounterpartyRef(String counterpartyRef) {
        this.counterpartyRef = counterpartyRef;
    }

    public String getCounterpartyName() {
        return counterpartyName;
    }

    public void setCounterpartyName(String counterpartyName) {
        this.counterpartyName = counterpartyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public LogKustodiKasDTO() {
    }

    public LogKustodiKasDTO(
            Long id,
            Long journalHeaderId,
            LocalDateTime transactionDate,
            String journalNo,
            String transactionNo,
            String transactionCode,
            String branchCode,
            String direction,
            BigDecimal amount,
            Long tellerUserId,
            String kasPointCode,
            String counterpartyType,
            String counterpartyRef,
            String counterpartyName,
            String description,
            LocalDateTime occurredAt
    ) {
        this.id = id;
        this.journalHeaderId = journalHeaderId;
        this.transactionDate = transactionDate;
        this.journalNo = journalNo;
        this.transactionNo = transactionNo;
        this.transactionCode = transactionCode;
        this.branchCode = branchCode;
        this.direction = direction;
        this.amount = amount;
        this.tellerUserId = tellerUserId;
        this.kasPointCode = kasPointCode;
        this.counterpartyType = counterpartyType;
        this.counterpartyRef = counterpartyRef;
        this.counterpartyName = counterpartyName;
        this.description = description;
        this.occurredAt = occurredAt;
    }

    // getter
}