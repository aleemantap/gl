package com.project.gl.model.entity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
// import org.apache.poi.hpsf.Decimal;

@Entity
@Table(
    name = "product_rule_component",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_product_rule_component",
            columnNames = {"product_rule_id", "component_code"}
        )
    },
    indexes = {
        @Index(
            name = "idx_prc_product_rule",
            columnList = "product_rule_id"
        )
    }
)
public class ProductRuleComponent extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_rule_id", nullable = false)
    private Long productRuleId;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "product_rule_id", nullable = false)
    // private ProductRule productRule;

   
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "component_id", nullable = false)
    // private Component component;


    @Column(name = "component_id", nullable = false)
    private Long componentId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Component component;



    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "transaction_code_id")
    // private KodeTransaksi transactionCode;
    

    @Column(name = "calculation_type", length = 30)
    private String calculationType;

    @Column(name = "amount_source", length = 50)
    private String amountSource;

    @Column(name = "fixed_amount", precision = 19, scale = 2)
    private BigDecimal fixedAmount;


    //@Column(name = "cash_movement_type", length = 30)
    //private String cashMovementType;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    

    
    public Long getComponentId() {
        return componentId;
    }

    public void setComponentId(Long componentId) {
        this.componentId = componentId;
    }

    
    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductRuleId() {
        return productRuleId;
    }

    public void setProductRuleId(Long productRuleId) {
        this.productRuleId = productRuleId;
    }

  
    public String getCalculationType() {
        return calculationType;
    }

    public void setCalculationType(String calculationType) {
        this.calculationType = calculationType;
    }

    public String getAmountSource() {
        return amountSource;
    }

    public void setAmountSource(String amountSource) {
        this.amountSource = amountSource;
    }

    // public String getCashMovementType() {
    //     return cashMovementType;
    // }

    // public void setCashMovementType(String cashMovementType) {
    //     this.cashMovementType = cashMovementType;
    // }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public BigDecimal getfixedAmount() {
        return fixedAmount;
    }

    public void setfixedAmount(BigDecimal fixedAmount) {
        this.fixedAmount = fixedAmount;
    }

}