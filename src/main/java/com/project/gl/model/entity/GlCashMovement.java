package com.project.gl.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Locale; 
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "gl_cash_movement")
public class GlCashMovement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "journal_header_id", nullable = false)
    private Long journalHeaderId;

    @Column(name = "direction", nullable = false, length = 10)
    private String direction;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "teller_user_id")
    private Long tellerUserId;

    @Column(name = "kas_point_code", length = 50)
    private String kasPointCode;

    @Column(name = "counterparty_type", length = 30)
    private String counterpartyType;

    @Column(name = "counterparty_ref", length = 100)
    private String counterpartyRef;

    @Column(name = "counterparty_name", length = 150)
    private String counterpartyName;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "description", length = 255)
    private String description;


    // =========================================================
    // GETTER & SETTER
    // =========================================================

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

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmountFormatted() {

        if (amount == null) {
            return "Rp 0";
        }

        return "Rp " + String.format(
                new Locale("id", "ID"),
                "%,.0f",
                amount
        ).replace(',', '.');
    }
}