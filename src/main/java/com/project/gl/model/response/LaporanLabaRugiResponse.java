package com.project.gl.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaporanLabaRugiResponse {

  

    private String kodePos;
    private String namaPos;
    private Long parentId;
    private Integer urutan;
    private Boolean isTotal;

    private BigDecimal bulanIni;
    private BigDecimal tahunBerjalan;

    @Builder.Default
    private List<LaporanLabaRugiResponse> children = new ArrayList<>();
}