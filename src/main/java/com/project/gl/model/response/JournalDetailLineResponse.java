package com.project.gl.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JournalDetailLineResponse {

    private Long id;
    private Long glAccountId;
    private String glAccountCode;
    private String glAccountName;
    private String dk;
    private BigDecimal amount;
    private String description;
}