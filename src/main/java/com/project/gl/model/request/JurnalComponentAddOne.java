package com.project.gl.model.request;

import lombok.Data;
@Data
public class JurnalComponentAddOne {
    private Long id;  //jlt ID
    private Long jurnalId;
    private Long nomorglcmpId;
    private String dkcmp;
    private Long componentRuleId;
    private String typePergerakanCash;
    private Boolean status;
    
}
