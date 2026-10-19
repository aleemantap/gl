package com.project.gl.model.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CabangResponse {
    private Integer id;
    private String kodeCabang;
    private String namaCabang;
    private String alamat;
    private String telepon;
    private String status;
}