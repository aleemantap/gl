package com.project.gl.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KategoriResponse {
    private Integer id;
    private String name;
    private String note;
}
