package com.project.gl.model.request;

import lombok.Data;

@Data
public class GlItemRequest {

    private Long id; // gl id
    private String dk;
    private Long componentRuleId;
    private String cashMovementType;
}