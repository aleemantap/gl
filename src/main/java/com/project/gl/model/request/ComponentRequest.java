package com.project.gl.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComponentRequest {
    private String componentCode;
    private String componentName;
    private String description;
    private Boolean isActive;
}