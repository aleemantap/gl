package com.project.gl.model.request;

public class NomorGlFilterRequest {

    private String nomorGl;
    private String namaGlAccount;
    private Integer typeId;
    // private Integer cabangId;
    private String saldoNormal;

    public String getNomorGl() {
        return normalize(nomorGl);
    }

    public void setNomorGl(String nomorGl) {
        this.nomorGl = nomorGl;
    }

    public String getNamaGlAccount() {
        return normalize(namaGlAccount);
    }

    public void setNamaGlAccount(String namaGlAccount) {
        this.namaGlAccount = namaGlAccount;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }



    public String getSaldoNormal() {
        return normalize(saldoNormal);
    }

    public void setSaldoNormal(String saldoNormal) {
        this.saldoNormal = saldoNormal;
    }

    private String normalize(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }
}