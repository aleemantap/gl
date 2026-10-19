package com.project.gl.model.response;
import java.math.BigDecimal;
import com.project.gl.model.entity.JournalLineTemplate;
import com.project.gl.model.entity.NomorGlAccount;
import com.project.gl.model.entity.ProductRuleComponent;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PreparedJournalLineDto {

    private JournalLineTemplate template;
    private ProductRuleComponent component;
    private NomorGlAccount glAccount;
    private BigDecimal amount;
    private String dk;
}