package com.project.gl.service;

import com.project.gl.model.request.OjkRatioCalculationRequest;
import com.project.gl.model.response.OjkRatioCalculationResult;
import com.project.gl.model.entity.OjkRatioConfig;
import com.project.gl.model.entity.OjkRatioDetail;
import com.project.gl.model.entity.OjkRatioReport;
import com.project.gl.model.enums.OjkRatioRegion;
import com.project.gl.model.enums.OjkRatioStatus;
import com.project.gl.repo.OjkRatioAccountMappingRepository;
import com.project.gl.model.projection.OjkRatioBalanceProjection;
import com.project.gl.repo.OjkRatioConfigRepository;
import com.project.gl.repo.OjkRatioDetailRepository;
import com.project.gl.repo.OjkRatioReportRepository;
import com.project.gl.service.OjkRatioCalculationEngine;
import com.project.gl.service.OjkRatioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.project.gl.model.projection.OjkProfitLossProjection;
import com.project.gl.repo.JournalHeaderRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class OjkRatioServiceImpl implements OjkRatioService {

    private final OjkRatioAccountMappingRepository mappingRepository;
    private final OjkRatioConfigRepository configRepository;
    private final OjkRatioReportRepository reportRepository;
    private final OjkRatioCalculationEngine calculationEngine;
    private final JournalHeaderRepository journalHeaderRepository;
    private final OjkRatioDetailRepository detailRepository;
    

    @Override
    public OjkRatioReport calculate(OjkRatioCalculationRequest request) {

        // Gunakan tanggal akhir bulan sebagai tanggal cut-off laporan.
        LocalDate periode = YearMonth.from(request.getPeriode()).atEndOfMonth();

        // Ambil saldo GL berdasarkan mapping akun.
        Map<String, BigDecimal> balances = getGlBalances(periode);

        //BigDecimal totalEquity = getBalance(balances, "TOTAL_EQUITY");
        // BigDecimal paidUpCapital = getBalance(balances, "PAID_UP_CAPITAL");
        // BigDecimal pawnLoanAmount = getBalance(balances, "PAWN_LOAN");


        // 1. Ambil modal disetor dari mapping GL
        BigDecimal paidUpCapital =  getBalance(balances, "PAID_UP_CAPITAL");

        // 2. Ambil laba/rugi berjalan dari akun revenue dan expense
        OjkProfitLossProjection profitLoss = getProfitLoss(periode);

        BigDecimal revenueAmount = zeroIfNull(profitLoss.getRevenueAmount());

        BigDecimal expenseAmount = zeroIfNull(profitLoss.getExpenseAmount());

        // 3. Hitung laba/rugi berjalan
        BigDecimal profitLossAmount = revenueAmount.subtract(expenseAmount);

        // 4. Hitung total ekuitas sementara
        BigDecimal totalEquity = paidUpCapital.add(profitLossAmount);

        // 5. Ambil saldo pinjaman
        BigDecimal pawnLoanAmount  = getBalance(balances, "PAWN_LOAN");




        BigDecimal fiduciaryLoan = getBalance(balances, "FIDUCIARY_LOAN");
        BigDecimal otherApprovedLoan = getBalance(balances, "OTHER_APPROVED_LOAN");

        // Pinjaman fidusia pemerintah tidak masuk denominator rasio 51%.
        BigDecimal totalLoanAmount = pawnLoanAmount
                .add(fiduciaryLoan)
                .add(otherApprovedLoan);

        validateSourceData(
                totalEquity,
                paidUpCapital,
                pawnLoanAmount,
                totalLoanAmount
        );

        // Cari laporan periode ini. Jika belum ada, buat baru.
        OjkRatioReport report = reportRepository.findByPeriode(periode)
                .orElseGet(OjkRatioReport::new);

        if (report.getId() != null
                && report.getStatus() == OjkRatioStatus.APPROVED) {
            throw new IllegalStateException(
                    "Laporan periode ini sudah APPROVED dan tidak dapat dihitung ulang."
            );
        }

        report.setReportNo(generateReportNo(periode));
        report.setPeriode(periode);
        report.setRegionScope(request.getRegionScope());

        report.setTotalEquity(totalEquity);
        report.setPaidUpCapital(paidUpCapital);

       report.setNotes(
                "Perhitungan ekuitas:\n"
                + "Modal Disetor: " + formatRupiah(paidUpCapital) + "\n"
                + "Laba/Rugi Berjalan: " + formatRupiah(profitLossAmount) + "\n"
                + "Pendapatan: " + formatRupiah(revenueAmount) + "\n"
                + "Beban: " + formatRupiah(expenseAmount)
        );

        report.setPawnLoanAmount(pawnLoanAmount);
        report.setTotalLoanAmount(totalLoanAmount);

        report.setStatus(OjkRatioStatus.DRAFT);
        report.setCalculatedAt(LocalDateTime.now());
        report.setCalculatedBy("SYSTEM");

        // Hapus detail lama ketika laporan DRAFT dihitung ulang.
        // report.getDetails().clear();

        if (report.getId() != null) {
            detailRepository.deleteByReportId(report.getId());
            detailRepository.flush();
        }

        report.getDetails().clear();


        // 1. Rasio modal sendiri minimum.
        addRatioDetail(
                report,
                "MINIMUM_EQUITY",
                request.getRegionScope(),
                periode,
                totalEquity,
                BigDecimal.ONE
        );

        // 2. Modal sendiri dibanding modal disetor.
        addRatioDetail(
                report,
                "EQUITY_TO_PAID_UP_CAPITAL",
                request.getRegionScope(),
                periode,
                totalEquity,
                paidUpCapital
        );

        // 3. Pinjaman gadai dibanding total pinjaman.
        addRatioDetail(
                report,
                "PAWN_LOAN_RATIO",
                request.getRegionScope(),
                periode,
                pawnLoanAmount,
                totalLoanAmount
        );

        // BMPP belum dihitung pada tahap ini karena belum ada sumber
        // saldo pinjaman per nasabah / kelompok nasabah yang teridentifikasi.

        return reportRepository.save(report);
    }

    private Map<String, BigDecimal> getGlBalances(LocalDate periode) {

        List<OjkRatioBalanceProjection> rows =
                mappingRepository.findGlBalancesByPeriode(periode);

        Map<String, BigDecimal> balances = new HashMap<>();

        for (OjkRatioBalanceProjection row : rows) {
            balances.put(
                    row.getComponentCode(),
                    row.getBalanceAmount() != null
                            ? row.getBalanceAmount()
                            : BigDecimal.ZERO
            );
        }

        return balances;
    }

    private BigDecimal getBalance(
            Map<String, BigDecimal> balances,
            String componentCode
    ) {
        return balances.getOrDefault(componentCode, BigDecimal.ZERO);
    }

    private void validateSourceData(
            BigDecimal totalEquity,
            BigDecimal paidUpCapital,
            BigDecimal pawnLoanAmount,
            BigDecimal totalLoanAmount
    ) {
        if (paidUpCapital.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException(
                    "Saldo modal disetor belum tersedia atau bernilai nol. "
                    + "Periksa mapping PAID_UP_CAPITAL."
            );
        }


        if (totalEquity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException(
                    "Total ekuitas bernilai nol atau negatif. "
                    + "Periksa modal disetor dan perhitungan laba/rugi."
            );
        }



        if (totalLoanAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException(
                    "Total pinjaman bernilai nol. Periksa mapping akun pinjaman."
            );
        }
    }

    private void addRatioDetail(
            OjkRatioReport report,
            String ratioCode,
            OjkRatioRegion regionScope,
            LocalDate periode,
            BigDecimal numerator,
            BigDecimal denominator
    ) {
        // OjkRatioConfig config = configRepository
        //         .findApplicableConfig(ratioCode, regionScope, periode)
        //         .orElseThrow(() -> new IllegalStateException(
        //                 "Konfigurasi rasio " + ratioCode
        //                 + " tidak ditemukan untuk periode " + periode
        //         ));
        OjkRatioConfig config = configRepository
        .findApplicableConfig(ratioCode, regionScope, periode)
        .stream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(
                "Konfigurasi rasio " + ratioCode
                + " tidak ditemukan untuk periode " + periode
        ));

        if (config.getUnit().name().equals("PERCENT")
                && denominator.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException(
                    "Denominator rasio " + ratioCode + " tidak boleh nol."
            );
        }

        OjkRatioCalculationResult result =
                calculationEngine.calculateRatio(
                        ratioCode,
                        config.getRatioName(),
                        numerator,
                        denominator,
                        config
                );

        OjkRatioDetail detail = new OjkRatioDetail();

        detail.setConfig(config);
        detail.setRatioCode(result.getRatioCode());
        detail.setRatioName(result.getRatioName());

        detail.setSubjectType(
                com.project.gl.model.enums.OjkRatioSubjectType.NONE
        );
        detail.setSubjectRef("");
        detail.setSubjectName(null);

        detail.setNumerator(result.getNumerator());
        detail.setDenominator(result.getDenominator());
        detail.setResultValue(result.getResultValue());
        detail.setThresholdValue(result.getThresholdValue());
        detail.setComparisonOperator(result.getComparisonOperator());
        detail.setUnit(result.getUnit());
        detail.setStatus(result.getStatus());
        detail.setCalculationNote(result.getCalculationNote());

        report.addDetail(detail);
    }

    private String generateReportNo(LocalDate periode) {
        return "OJK-RATIO-" +
                periode.format(DateTimeFormatter.ofPattern("yyyyMM"));
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public OjkRatioReport getReport(LocalDate periode) {
        LocalDate reportPeriode =
                YearMonth.from(periode).atEndOfMonth();

        return reportRepository.findByPeriode(reportPeriode)
                .orElseThrow(() -> new IllegalStateException(
                        "Laporan rasio OJK periode " + reportPeriode
                        + " belum tersedia."
                ));
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<OjkRatioReport> getAllReports() {
        return reportRepository.findAllByOrderByPeriodeDesc();
    }

    @Override
    public OjkProfitLossProjection getProfitLoss(LocalDate periode) {

        LocalDate tanggalMulai = periode.withDayOfYear(1);
        LocalDate tanggalSelesai = periode.plusDays(1);

        OjkProfitLossProjection result = journalHeaderRepository.findProfitLossForOjk(
                        tanggalMulai.atStartOfDay(),
                        tanggalSelesai.atStartOfDay()
                );

        return result;
    }

    @Override 
    public BigDecimal zeroIfNull(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }


    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public OjkRatioReport getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException(
                        "Laporan Rasio OJK tidak ditemukan."
                ));
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<OjkRatioDetail> getReportDetails(Long reportId) {

        return detailRepository.findByReportIdOrderByRatioCodeAsc(reportId);
    }

   private String formatRupiah(BigDecimal amount) {
        java.text.NumberFormat formatter =
                java.text.NumberFormat.getCurrencyInstance(
                        new java.util.Locale("id", "ID")
                );

        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(0);

        return formatter.format(amount);
    }
}