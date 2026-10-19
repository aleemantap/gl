package com.project.gl.service;

import com.project.gl.model.response.BukuBesarPageResponse;
import com.project.gl.model.response.BukuBesarResponse;
import com.project.gl.model.response.PageResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface BukuBesarService {

    // PageResponse<BukuBesarResponse> getBukuBesar(
    //         Long glAccountId,
    //         LocalDateTime tanggalDari,
    //         LocalDateTime tanggalSampai,
    //         String branchCode,
    //         int page,
    //         int size
    // );

    BukuBesarPageResponse getBukuBesar(
        String listIds, 
        Long glAccountId,
        LocalDateTime tanggalDari,
        LocalDateTime tanggalSampai,
        String branchCode);
   
}