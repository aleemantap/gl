package com.project.gl.model.request;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class JournalRequest {

    private String typeTransaction;
    private String transactionCode;
    private String transactionNo;
    private String branchCode;
    private OffsetDateTime occurredAt;

    private Actor actor;
    private Counterparty counterparty;
    private Amounts amounts;
    private Collateral collateral;
    private Flags flags;

    private String idempotencyKey;
    private String productCode;
    private String bankCode;

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }


    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getTypeTransaction() {
        return typeTransaction;
    }

    public void setTypeTransaction(String typeTransaction) {
        this.typeTransaction = typeTransaction;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Flags getFlags() {
        return flags;
    }

    public void setFlags(Flags flags) {
        this.flags = flags;
    }
 

    public Collateral getCollateral() {
        return collateral;
    }

    public void setCollateral(Collateral collateral) {
        this.collateral = collateral;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public OffsetDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(OffsetDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public Counterparty getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(Counterparty counterparty) {
        this.counterparty = counterparty;
    }

    public Amounts getAmounts() {
        return amounts;
    }

    public void setAmounts(Amounts amounts) {
        this.amounts = amounts;
    }

    // ===== INNER CLASS ACTOR =====
    public static class Actor {
        private Long tellerUserId;
        private String kasPointCode;

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
    }

    // ===== INNER CLASS COUNTERPARTY =====
    public static class Counterparty {
        private String type;
        private String ref;
        private String name;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getRef() {
            return ref;
        }

        public void setRef(String ref) {
            this.ref = ref;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // ===== INNER CLASS AMOUNTS =====
    public static class Amounts {


        // private BigDecimal amount;

        // @JsonProperty("admin_cost")
        // private BigDecimal adminCost;
        // private BigDecimal provision;

        // @JsonProperty("nasabah_terima_cash")
        // private BigDecimal nasabahTerimaCash;

        // public BigDecimal getAmount() {
        //     return amount;
        // }

        // public void setAmount(BigDecimal amount) {
        //     this.amount = amount;
        // }

        // public BigDecimal getAdminCost() {
        //     return adminCost;
        // }

        // public void setAdminCost(BigDecimal adminCost) {
        //     this.adminCost = adminCost;
        // }

        // public BigDecimal getProvision() {
        //     return provision;
        // }

        // public void setProvision(BigDecimal provision) {
        //     this.provision = provision;
        // }

        // public BigDecimal getNasabahTerimaCash() {
        //     return nasabahTerimaCash;
        // }

        // public void setNasabahTerimaCash(BigDecimal nasabahTerimaCash) {
        //     this.nasabahTerimaCash = nasabahTerimaCash;
        // }

        @JsonProperty("loan_amount")
        private BigDecimal loanAmount;

        @JsonProperty("repayment_amount")
        private BigDecimal repaymentAmount;

        @JsonProperty("interest_amount")
        private BigDecimal interestAmount;

        @JsonProperty("admin_cost")
        private BigDecimal adminCost;

        @JsonProperty("provision")
        private BigDecimal provision;

        @JsonProperty("customer_cash")
        private BigDecimal customerCash;

        @JsonProperty("payment_amount")
        private BigDecimal paymentAmount;

        @JsonProperty("principal_payment")
        private BigDecimal principalPayment;

        @JsonProperty("customer_bank")
        private BigDecimal customerBank;

        public BigDecimal getCustomerBank() {
            return customerBank;
        }

        public void setCustomerBank(BigDecimal customerBank) {
            this.customerBank = customerBank;
        }


        public BigDecimal getPaymentAmount() {
            return paymentAmount;
        }

        public void setPaymentAmount(BigDecimal paymentAmount) {
            this.paymentAmount = paymentAmount;
        }

        public BigDecimal getPrincipalPayment() {
            return principalPayment;
        }

        public void setPrincipalPayment(BigDecimal principalPayment) {
            this.principalPayment = principalPayment;
        }

        public BigDecimal getLoanAmount() {
            return loanAmount;
        }

        public void setLoanAmount(BigDecimal loanAmount) {
            this.loanAmount = loanAmount;
        }


        public BigDecimal getRepaymentAmount() {
            return repaymentAmount;
        }

        public void setRepaymentAmount(BigDecimal repaymentAmount) {
            this.repaymentAmount = repaymentAmount;
        }


        public BigDecimal getInterestAmount() {
            return interestAmount;
        }

        public void setInterestAmount(BigDecimal interestAmount) {
            this.interestAmount = interestAmount;
        }


        public BigDecimal getAdminCost() {
            return adminCost;
        }

        public void setAdminCost(BigDecimal adminCost) {
            this.adminCost = adminCost;
        }


        public BigDecimal getProvision() {
            return provision;
        }

        public void setProvision(BigDecimal provision) {
            this.provision = provision;
        }


        public BigDecimal getCustomerCash() {
            return customerCash;
        }

        public void setCustomerCash(BigDecimal customerCash) {
            this.customerCash = customerCash;
        }
    }

    // ===== INNER CLASS FLAG =====
    public static class Flags {

        @JsonProperty("admin_in_advance")
        private Boolean adminInAdvance;

        @JsonProperty("provision_in_advance")
        private Boolean provisionInAdvance;

        public Boolean getAdminInAdvance() {
            return adminInAdvance;
        }

        public void setAdminInAdvance(Boolean adminInAdvance) {
            this.adminInAdvance = adminInAdvance;
        }

        public Boolean getProvisionInAdvance() {
            return provisionInAdvance;
        }

        public void setProvisionInAdvance(Boolean provisionInAdvance) {
            this.provisionInAdvance = provisionInAdvance;
        }
    }

    // ===== INNER CLASS COLLATERAL =====
    public static class Collateral {

        @JsonProperty("collateral_type")
        private String collateralType;

        @JsonProperty("appraised_value")
        private BigDecimal appraisedValue;

        private BigDecimal ltv;

        public String getCollateralType() {
            return collateralType;
        }

        public void setCollateralType(String collateralType) {
            this.collateralType = collateralType;
        }

        public BigDecimal getAppraisedValue() {
            return appraisedValue;
        }

        public void setAppraisedValue(BigDecimal appraisedValue) {
            this.appraisedValue = appraisedValue;
        }

        public BigDecimal getLtv() {
            return ltv;
        }

        public void setLtv(BigDecimal ltv) {
            this.ltv = ltv;
        }
    }


}

