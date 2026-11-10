package com.project.gl.service;

import com.project.gl.model.response.GlCashMovementDetailDto;
import com.project.gl.model.response.LaporanKasHarianDto;
import com.project.gl.model.projection.GlCashBalanceProjection;
import com.project.gl.model.projection.GlCashMovementDetailProjection;
import com.project.gl.model.projection.GlCashMovementTotalProjection;
import com.project.gl.repo.JournalDetailRepository;
import com.project.gl.service.LaporanKasHarianService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LaporanKasHarianServiceImpl implements LaporanKasHarianService {

    private final JournalDetailRepository journalDetailRepository;

    private static final Long GL_KAS = 11L;
    private static final Long GL_KAS_KECIL = 34L;

    @Override
    public LaporanKasHarianDto getLaporan(Long cabangId, LocalDate tanggal) {

        if (cabangId == null) {
            throw new IllegalArgumentException("Cabang wajib dipilih");
        }

        if (tanggal == null) {
            throw new IllegalArgumentException("Tanggal laporan wajib diisi");
        }

        LocalDateTime awalHari = tanggal.atStartOfDay();
        LocalDateTime awalHariBerikutnya = tanggal.plusDays(1).atStartOfDay();

        LaporanKasHarianDto dto = new LaporanKasHarianDto();

        dto.setCabangId(cabangId);
        dto.setTanggal(tanggal);

        // 1. Saldo awal: seluruh jurnal sebelum tanggal laporan
        List<GlCashBalanceProjection> saldoAwalList =
                journalDetailRepository.findSaldoKasByCabang(
                        cabangId,
                        awalHari
                );

        Map<Long, BigDecimal> saldoAwal = toSaldoMap(saldoAwalList);

        dto.setSaldoAwalKas(
                saldoAwal.getOrDefault(GL_KAS, BigDecimal.ZERO)
        );

        dto.setSaldoAwalKasKecil(
                saldoAwal.getOrDefault(GL_KAS_KECIL, BigDecimal.ZERO)
        );

        // 2. Total kas masuk dan keluar pada tanggal laporan
        List<GlCashMovementTotalProjection> movementList =
                journalDetailRepository.findTotalKasHarian(
                        cabangId,
                        awalHari,
                        awalHariBerikutnya
                );

        // 2b. Rincian kas berdasarkan jenis transaksi
        List<GlCashMovementDetailProjection> detailList =
                journalDetailRepository.findDetailKasHarian(
                        cabangId,
                        awalHari,
                        awalHariBerikutnya
                );

        Map<String, GlCashMovementDetailDto> detailMap = new HashMap<>();
        for (GlCashMovementDetailProjection item : detailList) {

            String key = item.getTransactionCode();

            GlCashMovementDetailDto detail = detailMap.computeIfAbsent(
                    key,
                    k -> {
                        GlCashMovementDetailDto dtoDetail =
                                new GlCashMovementDetailDto();

                        dtoDetail.setTransactionCode(item.getTransactionCode());
                        dtoDetail.setTransactionName(item.getTransactionName());

                        return dtoDetail;
                    }
            );

            BigDecimal amount = item.getTotalAmount() != null
                    ? item.getTotalAmount()
                    : BigDecimal.ZERO;

            if ("IN".equalsIgnoreCase(item.getDirection())) {
                detail.setKasMasuk(detail.getKasMasuk().add(amount));

            } else if ("OUT".equalsIgnoreCase(item.getDirection())) {
                detail.setKasKeluar(detail.getKasKeluar().add(amount));
            }
        }

        dto.setDetails(new ArrayList<>(detailMap.values()));        
        

        BigDecimal kasMasuk = BigDecimal.ZERO;
        BigDecimal kasKeluar = BigDecimal.ZERO;

        for (GlCashMovementTotalProjection movement : movementList) {

            BigDecimal amount = movement.getTotalAmount() != null
                    ? movement.getTotalAmount()
                    : BigDecimal.ZERO;

            if ("IN".equalsIgnoreCase(movement.getDirection())) {
                kasMasuk = kasMasuk.add(amount);
            } else if ("OUT".equalsIgnoreCase(movement.getDirection())) {
                kasKeluar = kasKeluar.add(amount);
            }
        }

        dto.setKasMasuk(kasMasuk);
        dto.setKasKeluar(kasKeluar);

        // 3. Saldo akhir: seluruh jurnal sampai akhir tanggal laporan
        List<GlCashBalanceProjection> saldoAkhirList =
                journalDetailRepository.findSaldoKasByCabang(
                        cabangId,
                        awalHariBerikutnya
                );

        Map<Long, BigDecimal> saldoAkhir = toSaldoMap(saldoAkhirList);

        dto.setSaldoAkhirKas(
                saldoAkhir.getOrDefault(GL_KAS, BigDecimal.ZERO)
        );

        dto.setSaldoAkhirKasKecil(
                saldoAkhir.getOrDefault(GL_KAS_KECIL, BigDecimal.ZERO)
        );

        // 4. Total saldo akhir kas cabang
        dto.setTotalSaldoAkhir(
                dto.getSaldoAkhirKas()
                        .add(dto.getSaldoAkhirKasKecil())
        );

        return dto;
    }

    private Map<Long, BigDecimal> toSaldoMap(
            List<GlCashBalanceProjection> saldoList
    ) {
        Map<Long, BigDecimal> result = new HashMap<>();

        for (GlCashBalanceProjection item : saldoList) {
            result.put(
                    item.getGlAccountId(),
                    item.getSaldo() != null
                            ? item.getSaldo()
                            : BigDecimal.ZERO
            );
        }

        return result;
    }
}