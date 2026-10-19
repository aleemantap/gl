package com.project.gl.model.request;

import java.util.List;

import lombok.Data;


@Data
public class JournalAddRequest {
    
    private String journalCode;
    private String journalId;
    private Integer jr_id;
    private String catatan;
    private List<GlItemRequest> glItems;
    
}
