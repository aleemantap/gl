package com.project.gl.service;

import com.project.gl.model.response.BukuBesarPageResponse;
import com.project.gl.model.response.BukuBesarResponse;
import com.project.gl.model.response.PageResponse;
import com.project.gl.repo.BukuBesarRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.gl.helper.Util;
import com.project.gl.model.projection.BukuBesarProjection;
import com.project.gl.model.projection.SaldoAwalProjection;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BukuBesarServiceImpl implements BukuBesarService {

    private final BukuBesarRepository bukuBesarRepository;
//     private final ObjectMapper objectMapper;
    private final Util util;
    

    @Override
    public BukuBesarPageResponse getBukuBesar(
            String listIds, 
            Long glAccountId,
            LocalDateTime tanggalDari,
            LocalDateTime tanggalSampai,
            String branchCode
    ) {

    List<Long> ids = util.parseIdsJson(listIds);    

    // ==========================================
    // 1. SALDO AWAL
    // ==========================================

    SaldoAwalProjection saldoData =
            bukuBesarRepository.findSaldoAwal(
                    glAccountId,
                    tanggalDari,
                    branchCode
            );

    BigDecimal totalDebitSebelum =
            saldoData != null && saldoData.getTotalDebit() != null
                    ? saldoData.getTotalDebit()
                    : BigDecimal.ZERO;

    BigDecimal totalCreditSebelum =
            saldoData != null && saldoData.getTotalCredit() != null
                    ? saldoData.getTotalCredit()
                    : BigDecimal.ZERO;

    String normalBalance =
            saldoData != null
                    ? saldoData.getNormalBalance()
                    : null;

    BigDecimal saldoAwal;

    if ("D".equalsIgnoreCase(normalBalance)) {

        saldoAwal =
                totalDebitSebelum
                        .subtract(totalCreditSebelum);

    } else {

        saldoAwal =
                totalCreditSebelum
                        .subtract(totalDebitSebelum);
    }

    // ==========================================
    // 2. TRANSAKSI PERIODE
    // ==========================================

    boolean idsEmpty = (ids == null || ids.isEmpty());
    List<Long> safeIds = idsEmpty ? List.of(-1L) : ids; 

    List<BukuBesarProjection> rows =  bukuBesarRepository.findBukuBesar(
                    safeIds, 
                    idsEmpty, 
                    glAccountId,
                    tanggalDari,
                    tanggalSampai,
                    branchCode
            );
        // System.out.println("glAccountId"+glAccountId);
        // System.out.println("tanggalDari"+tanggalDari);
        // System.out.println("tanggalSampai"+tanggalSampai);  
        //  System.out.println("branchCode"+branchCode);    
        //   System.out.println("rows"+rows.size());    
    // ==========================================
    // 3. MAPPING DTO
    // ==========================================
    List<BukuBesarResponse> data =
            rows.stream()
                    .map(p -> new BukuBesarResponse(
                            p.getDetailId(),
                            p.getJournalHeaderId(),
                            p.getTransactionDate(),
                            p.getJournalNo(),
                            p.getTransactionNo(),
                            p.getTransactionCode(),
                            p.getBranchCode(),
                            p.getBranchName(),
                            p.getGlAccountId(),
                            p.getAccountCode(),
                            p.getAccountName(),
                            p.getNormalBalance(),
                            p.getDk(),
                            p.getAmount(),
                            p.getDescription()
                    ))
                    .toList();

 

    // ==========================================
    // 4. RESPONSE
    // ==========================================

        return new BukuBesarPageResponse(
                                saldoAwal,
                                normalBalance,
                                data
                        );
        }
       
}