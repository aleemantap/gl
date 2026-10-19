package com.project.gl.model.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BukuBesarPageResponse {

    private BigDecimal saldoAwal;

    private String normalBalance;

    private List<BukuBesarResponse> data;
}