package com.project.gl.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "approval_group")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    /**
     * Group pembuat (Maker)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maker_group_id", nullable = false)
    private UserGroup makerGroup;

    /**
     * Group yang berhak approve
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_group_id", nullable = false)
    private UserGroup approverGroup;

    @Column(name = "active")
    private String active = "Y";

    

   
}