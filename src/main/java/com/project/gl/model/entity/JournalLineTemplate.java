package com.project.gl.model.entity;


import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
@Entity
@Table(name = "journal_line_template")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JournalLineTemplate extends BaseEntity {

  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "journal_id", nullable = false)
    private Long journalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "product_rule_component_id",
        referencedColumnName = "id", insertable = false, updatable = false
    )
    private ProductRuleComponent productRuleComponent;

    @Column(name = "product_rule_component_id", nullable = false)
    private Long productRuleComponentId;

  
    @Column(name = "gl_account_id", nullable = false)
    private Long glAccountId;

    @Column(name = "dk", nullable = false, length = 1)
    private String dk;

    @Column(name = "line_order", nullable = false)
    private Integer lineOrder;

    @Column(name = "description")
    private String description;

    @Column(name = "cash_movement_type")
    private String cashMovementType;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // @Column(name = "created_at")
    // private LocalDateTime createdAt;

    // @Column(name = "created_by")
    // private String createdBy;

    // @Column(name = "updated_at")
    // private LocalDateTime updatedAt;

    // @Column(name = "updated_by")
    // private String updatedBy;


    
}