package com.project.gl.model.request;


import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupRequest {
    private Long id;
    private String namaGroup;
    private String keterangan;
    private String active;
}