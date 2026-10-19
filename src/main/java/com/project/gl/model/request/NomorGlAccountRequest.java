package com.project.gl.model.request;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NomorGlAccountRequest {
    private Long id;
    private String nomorGl;
    private String namaGlAccount;
    private Integer typeGlAccountId;
    private String saldoNormal;
    // private Integer cabangId;
}