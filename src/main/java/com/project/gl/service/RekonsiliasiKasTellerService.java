package com.project.gl.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.project.gl.model.entity.CashReconciliation;
import com.project.gl.model.response.CashReconciliationDto;
import com.project.gl.model.response.RekonsiliasiKasTellerDTO;

public interface RekonsiliasiKasTellerService {
    
     RekonsiliasiKasTellerDTO getRekonsiliasi(
            Long tellerUserId,
            String kasPointCode,
            LocalDate tanggal
    );

    CashReconciliationDto getReconciliation(
            Long tellerUserId,
            String kasPointCode,
            LocalDate date,
            BigDecimal openingBalance,
            BigDecimal physicalBalance
    );
    public CashReconciliation saveReconciliation(
                Long tellerUserId,
                String kasPointCode,
                LocalDate date,
                BigDecimal openingBalance,
                BigDecimal physicalBalance,
                String reconciledBy);
}
