package com.project.gl.model.response;
import java.math.BigDecimal;
import java.time.LocalDate;

public class RekonsiliasiKasTellerDTO {

    private Long id;
    private Long tellerUserId;
    private String kasPointCode;
    private LocalDate reconciliationDate;
    private String shiftCode;
    private BigDecimal openingCash;
    private BigDecimal cashAddition;
    private BigDecimal totalCashIn;
    private BigDecimal totalCashOut;
    private BigDecimal expectedCash;
    private BigDecimal actualCash;
    private BigDecimal difference;
    private String status;

    // Default / No-Args Constructor
    public RekonsiliasiKasTellerDTO() {
    }

    // All-Args Constructor
    public RekonsiliasiKasTellerDTO(Long id, Long tellerUserId, String kasPointCode, LocalDate reconciliationDate,
                                  String shiftCode, BigDecimal openingCash, BigDecimal cashAddition,
                                  BigDecimal totalCashIn, BigDecimal totalCashOut, BigDecimal expectedCash,
                                  BigDecimal actualCash, BigDecimal difference, String status) {
        this.id = id;
        this.tellerUserId = tellerUserId;
        this.kasPointCode = kasPointCode;
        this.reconciliationDate = reconciliationDate;
        this.shiftCode = shiftCode;
        this.openingCash = openingCash;
        this.cashAddition = cashAddition;
        this.totalCashIn = totalCashIn;
        this.totalCashOut = totalCashOut;
        this.expectedCash = expectedCash;
        this.actualCash = actualCash;
        this.difference = difference;
        this.status = status;
    }

    // --- Getter & Setter ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getReconciliationDate() {
        return reconciliationDate;
    }

    public void setReconciliationDate(LocalDate reconciliationDate) {
        this.reconciliationDate = reconciliationDate;
    }

    public String getShiftCode() {
        return shiftCode;
    }

    public void setShiftCode(String shiftCode) {
        this.shiftCode = shiftCode;
    }

    public BigDecimal getOpeningCash() {
        return openingCash;
    }

    public void setOpeningCash(BigDecimal openingCash) {
        this.openingCash = openingCash;
    }

    public BigDecimal getCashAddition() {
        return cashAddition;
    }

    public void setCashAddition(BigDecimal cashAddition) {
        this.cashAddition = cashAddition;
    }

    public BigDecimal getTotalCashIn() {
        return totalCashIn;
    }

    public void setTotalCashIn(BigDecimal totalCashIn) {
        this.totalCashIn = totalCashIn;
    }

    public BigDecimal getTotalCashOut() {
        return totalCashOut;
    }

    public void setTotalCashOut(BigDecimal totalCashOut) {
        this.totalCashOut = totalCashOut;
    }

    public BigDecimal getExpectedCash() {
        return expectedCash;
    }

    public void setExpectedCash(BigDecimal expectedCash) {
        this.expectedCash = expectedCash;
    }

    public BigDecimal getActualCash() {
        return actualCash;
    }

    public void setActualCash(BigDecimal actualCash) {
        this.actualCash = actualCash;
    }

    public BigDecimal getDifference() {
        return difference;
    }

    public void setDifference(BigDecimal difference) {
        this.difference = difference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}