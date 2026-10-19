package com.project.gl.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
// @AllArgsConstructor
public class JournalLineTemplateResponse {

  private Long id;
    private Long journalId;
    private Long glAccountId;

    private String nomorGl;
    private String namaGlAccount;
    private String glAccountType;

    private String dk;
    private Integer lineOrder;
    private String description;

    private String componentName;
    private String componentCode;
    private Long componentId;
    private Boolean isActive;   
    private Long productRuleId;

    private String cashMovementType;

    public JournalLineTemplateResponse(
            Long id,
            Long journalId,
            Long glAccountId,
            String nomorGl,
            String namaGlAccount,
            String glAccountType,
            String dk,
            Integer lineOrder,
            String description) {

        this.id = id;
        this.journalId = journalId;
        this.glAccountId = glAccountId;
        this.nomorGl = nomorGl;
        this.namaGlAccount = namaGlAccount;
        this.glAccountType = glAccountType;
        this.dk = dk;
        this.lineOrder = lineOrder;
        this.description = description;
    }


    public JournalLineTemplateResponse(
            Long id,
            Long journalId,
            Long glAccountId,
            String nomorGl,
            String namaGlAccount,
            String glAccountType,
            String dk,
            Integer lineOrder,
            String description,
            String componentName,
            String componentCode,
            Long componentId,
            Boolean isActive,
            Long productRuleId,
            String cashMovementType          
          ) {

            this.id = id;
            this.journalId = journalId;
            this.glAccountId = glAccountId;
            this.nomorGl = nomorGl;
            this.namaGlAccount = namaGlAccount;
            this.glAccountType = glAccountType;
            this.dk = dk;
            this.lineOrder = lineOrder;
            this.componentName = componentName;
            this.componentCode = componentCode;
            this.componentId = componentId;
            this.isActive = isActive;
            this.productRuleId = productRuleId;
            this.cashMovementType = cashMovementType;
    }
}