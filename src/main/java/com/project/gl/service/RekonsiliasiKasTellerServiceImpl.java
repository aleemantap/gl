package com.project.gl.service;


import com.project.gl.model.request.ProductRequest;
import com.project.gl.model.request.ProductRuleRequest;
import com.project.gl.model.response.CashReconciliationDto;
import com.project.gl.model.response.ProductDataTableResponse;
import com.project.gl.model.response.ProductResponse;
import com.project.gl.model.response.ProductRuleDetailResponse;
import com.project.gl.model.response.ProductRuleDTO;
import com.project.gl.model.response.ProductRuleSummary;
import com.project.gl.model.response.ProductWithRulesResponse;
import com.project.gl.model.response.RekonsiliasiKasTellerDTO;
import com.project.gl.model.entity.CashReconciliation;
import com.project.gl.model.entity.GlCashMovement;
import com.project.gl.model.entity.Product;
import com.project.gl.model.entity.ProductRule;
import com.project.gl.model.projection.RekonsiliasiKasTellerProjection;
import com.project.gl.repo.GlCashMovementRepository;
import com.project.gl.repo.ProductRepository;
import com.project.gl.repo.ProductRuleRepository;
import com.project.gl.service.ProductService;
import com.project.gl.repo.CashReconciliationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class RekonsiliasiKasTellerServiceImpl implements RekonsiliasiKasTellerService{
    
    @Autowired
    private GlCashMovementRepository glCashMovementRepository;

    @Autowired
    private CashReconciliationRepository cashReconciliationRepository;

    @Override
    public RekonsiliasiKasTellerDTO getRekonsiliasi(
            Long tellerUserId,
            String kasPointCode,
            LocalDate tanggal
    ) {

        LocalDateTime mulai = tanggal.atStartOfDay();
        LocalDateTime besok = tanggal.plusDays(1).atStartOfDay();
        RekonsiliasiKasTellerProjection p = glCashMovementRepository.getRekonsiliasiKasTeller(
                        tellerUserId,
                        kasPointCode,
                        mulai,
                        besok
                );

        // return new RekonsiliasiKasTellerDTO(
        //         tellerUserId,
        //         kasPointCode,
        //         tanggal,
        //         p.getSaldoAwal(),
        //         p.getTotalIn(),
        //         p.getTotalOut(),
        //         p.getSaldoSistem()
        // );

          return new RekonsiliasiKasTellerDTO();
    }


    @Override
    public CashReconciliationDto getReconciliation(
            Long tellerUserId,
            String kasPointCode,
            LocalDate date,
            BigDecimal openingBalance,
            BigDecimal physicalBalance) {

        LocalDateTime start = date.atStartOfDay();

        LocalDateTime end = date
                .plusDays(1)
                .atStartOfDay()
                .minusNanos(1);

        List<GlCashMovement> movements =
                glCashMovementRepository
                        .findByTellerUserIdAndKasPointCodeAndOccurredAtBetweenOrderByOccurredAtAsc(
                                tellerUserId,
                                kasPointCode,
                                start,
                                end
                        );

        BigDecimal totalIn = movements.stream()
                .filter(m -> "IN".equalsIgnoreCase(m.getDirection()))
                .map(GlCashMovement::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOut = movements.stream()
                .filter(m -> "OUT".equalsIgnoreCase(m.getDirection()))
                .map(GlCashMovement::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal systemBalance =
                openingBalance
                        .add(totalIn)
                        .subtract(totalOut);

        //Saldo Akhir = Saldo Awal + Total Masuk - Total Keluar

       // Breakdown Kode:
       //1.openingBalance = saldo awal (misal: uang yang ada di kasir saat mulai kerja)
       //2..add(totalIn) = tambahkan total uang masuk
       //3. .subtract(totalOut) = kurangi total uang keluar

        BigDecimal difference = BigDecimal.ZERO;

        String status = "BELUM REKONSILIASI";

        if (physicalBalance != null) {

            difference = physicalBalance.subtract(systemBalance);

            if (difference.compareTo(BigDecimal.ZERO) == 0) {
                status = "MATCH";
            } else {
                status = "SELISIH";
            }
        }

        CashReconciliationDto dto = new CashReconciliationDto();

        dto.setTellerUserId(tellerUserId);
        dto.setKasPointCode(kasPointCode);
        dto.setDate(date);
        dto.setOpeningBalance(openingBalance);
        dto.setTotalIn(totalIn);
        dto.setTotalOut(totalOut);
        dto.setSystemBalance(systemBalance);
        dto.setPhysicalBalance(physicalBalance);
        dto.setDifference(difference);
        dto.setStatus(status);
        dto.setMovements(movements);

        return dto;
    }

        @Transactional
        @Override
        public CashReconciliation saveReconciliation(
                Long tellerUserId,
                String kasPointCode,
                LocalDate date,
                BigDecimal openingBalance,
                BigDecimal physicalBalance,
                String reconciledBy) {

        if (physicalBalance == null) {
        throw new IllegalArgumentException(
                "Saldo kas fisik wajib diisi"
        );
        }
        if (openingBalance == null) {
        throw new IllegalArgumentException(
                "Saldo awal wajib diisi"
        );
        }

        if (tellerUserId == null) {
        throw new IllegalArgumentException(
                "Teller wajib diisi"
        );
        }

        if (kasPointCode == null || kasPointCode.isBlank()) {
        throw new IllegalArgumentException(
                "Kas point wajib diisi"
        );
        }

                                if (date == null) {
                                throw new IllegalArgumentException(
                                        "Tanggal wajib diisi"
                                );
                                }

                        
        // 1. Cek apakah sudah pernah direkonsiliasi
        Optional<CashReconciliation> existing =
                cashReconciliationRepository
                        .findByTellerUserIdAndKasPointCodeAndReconciliationDate(
                        tellerUserId,
                        kasPointCode,
                        date
                        );

        if (existing.isPresent()) {
                throw new IllegalStateException(
                "Kas teller untuk tanggal tersebut sudah direkonsiliasi"
                );
        }

        // 2. Ambil movement
        LocalDateTime start = date.atStartOfDay();

        LocalDateTime end = date
                .plusDays(1)
                .atStartOfDay()
                .minusNanos(1);

        List<GlCashMovement> movements = glCashMovementRepository
                        .findByTellerUserIdAndKasPointCodeAndOccurredAtBetweenOrderByOccurredAtAsc(
                        tellerUserId,
                        kasPointCode,
                        start,
                        end
                        );

                // 3. Hitung ulang
                BigDecimal totalIn = movements.stream()
                        .filter(x -> "IN".equalsIgnoreCase(x.getDirection()))
                        .map(GlCashMovement::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalOut = movements.stream()
                        .filter(x -> "OUT".equalsIgnoreCase(x.getDirection()))
                        .map(GlCashMovement::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal systemBalance =
                        openingBalance
                                .add(totalIn)
                                .subtract(totalOut);

                BigDecimal difference = physicalBalance.subtract(systemBalance);

                String status = difference.compareTo(BigDecimal.ZERO) == 0
                                ? "MATCH"
                                : "SELISIH";

                // 4. Simpan
                CashReconciliation reconciliation = new CashReconciliation();

                reconciliation.setTellerUserId(tellerUserId);
                reconciliation.setKasPointCode(kasPointCode);
                reconciliation.setReconciliationDate(date);
                reconciliation.setOpeningBalance(openingBalance);
                reconciliation.setTotalIn(totalIn);
                reconciliation.setTotalOut(totalOut);
                reconciliation.setSystemBalance(systemBalance);
                reconciliation.setPhysicalBalance(physicalBalance);
                reconciliation.setDifference(difference);
                reconciliation.setStatus(status);
                reconciliation.setReconciledBy(reconciledBy);
                reconciliation.setReconciledAt(LocalDateTime.now());

                return cashReconciliationRepository.save(reconciliation);
        }
}
