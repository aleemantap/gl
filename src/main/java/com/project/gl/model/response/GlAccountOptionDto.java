package com.project.gl.model.response;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GlAccountOptionDto {

    private Integer id;
    private String nomorGl;
    private String namaGlAccount;
    private String saldoNormal;
}