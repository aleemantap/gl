package com.project.gl.service;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import lombok.RequiredArgsConstructor;
import java.util.function.Function;
import com.project.gl.model.entity.JenisLaporanKeuangan;
import com.project.gl.model.entity.LaporanKeuanganPos;
import com.project.gl.model.projection.LaporanLabaRugiDetailProjection;
import com.project.gl.model.projection.LaporanLabaRugiProjection;
import com.project.gl.model.response.LaporanLabaRugiDetailResponse;
import com.project.gl.model.response.LaporanLabaRugiResponse;
import com.project.gl.repo.LaporanKeuanganPosRepository;
import com.project.gl.repo.LaporanLabaRugiRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LaporanLabaRugiServiceImpl  implements LaporanLabaRugiService {

    private final LaporanLabaRugiRepository laporanRepository;
    private final LaporanKeuanganPosRepository posRepository;

    @Override
    public List<LaporanLabaRugiResponse> getLaporan(
            YearMonth periode,
            Long cabangId) {

        LocalDateTime awalBulan = periode
                .atDay(1)
                .atStartOfDay();

        LocalDateTime akhirBulan = periode
                .plusMonths(1)
                .atDay(1)
                .atStartOfDay();

        LocalDateTime awalTahun = periode
                .withMonth(1)
                .atDay(1)
                .atStartOfDay();

        // 1. Ambil struktur pos laporan
        List<LaporanKeuanganPos> positions = posRepository.findByJenisLaporanAndIsActiveTrueOrderByUrutanAsc(
                        JenisLaporanKeuangan.LABA_RUGI
                    );

        // 2. Ambil saldo dari jurnal
        List<LaporanLabaRugiProjection> saldoList =
                laporanRepository.findSaldoLabaRugi(
                        awalBulan,
                        akhirBulan,
                        awalTahun,
                        cabangId
                );

        // 3. Siapkan map saldo berdasarkan ID pos
        Map<Long, LaporanLabaRugiProjection> saldoMap =
                saldoList.stream().collect(
                    Collectors.toMap(
                        LaporanLabaRugiProjection::getPosId,
                        Function.identity()
                    )
                );

        // 4. Buat DTO untuk semua pos
        Map<Long, LaporanLabaRugiResponse> responseMap =
                new LinkedHashMap<>();

        Map<Long, LaporanKeuanganPos> entityMap =
                new LinkedHashMap<>();

        for (LaporanKeuanganPos pos : positions) {

            entityMap.put(pos.getId(), pos);

            LaporanLabaRugiProjection saldo =
                    saldoMap.get(pos.getId());

            BigDecimal bulanIni = saldo != null
                    ? safe(saldo.getBulanIni())
                    : BigDecimal.ZERO;

            BigDecimal tahunBerjalan = saldo != null
                    ? safe(saldo.getTahunBerjalan())
                    : BigDecimal.ZERO;

            LaporanLabaRugiResponse response =
                    LaporanLabaRugiResponse.builder()
                        .kodePos(pos.getKodePos())
                        .namaPos(pos.getNamaPos())
                        .parentId(pos.getParentId())
                        .urutan(pos.getUrutan())
                        .isTotal(pos.getIsTotal())
                        .bulanIni(bulanIni)
                        .tahunBerjalan(tahunBerjalan)
                        .children(new ArrayList<>())
                        .build();

            responseMap.put(pos.getId(), response);
        }

       
        // 5. Hitung total berdasarkan parent
        for (LaporanKeuanganPos parent : positions) {

            // Cari apakah posisi ini memiliki anak
            List<LaporanKeuanganPos> children = positions.stream()
                    .filter(child -> Objects.equals(
                            child.getParentId(),
                            parent.getId()
                    ))
                    .toList();

            // Jangan proses posisi detail yang tidak memiliki anak
            if (children.isEmpty()) {
                continue;
            }

            BigDecimal totalBulan = BigDecimal.ZERO;
            BigDecimal totalTahun = BigDecimal.ZERO;

            LaporanKeuanganPos totalPos = null;

            for (LaporanKeuanganPos child : children) {

                LaporanLabaRugiResponse childResponse =
                        responseMap.get(child.getId());

                if (Boolean.TRUE.equals(child.getIsTotal())) {
                    totalPos = child;
                    continue;
                }

                totalBulan = totalBulan.add(
                        safe(childResponse.getBulanIni())
                );

                totalTahun = totalTahun.add(
                        safe(childResponse.getTahunBerjalan())
                );
            }

            // Isi baris total
            if (totalPos != null) {

                LaporanLabaRugiResponse totalResponse =
                        responseMap.get(totalPos.getId());

                totalResponse.setBulanIni(totalBulan);
                totalResponse.setTahunBerjalan(totalTahun);
            }

            // Isi parent
            LaporanLabaRugiResponse parentResponse =
                    responseMap.get(parent.getId());

            parentResponse.setBulanIni(totalBulan);
            parentResponse.setTahunBerjalan(totalTahun);
        }

        // 6. Hitung laba sebelum pajak
        LaporanLabaRugiResponse totalPendapatan =
                findByKode(responseMap, positions, "LR-01-T");

        LaporanLabaRugiResponse totalBeban =
                findByKode(responseMap, positions, "LR-02-T");

        LaporanLabaRugiResponse labaSebelumPajak =
                findByKode(responseMap, positions, "LR-03");

        if (totalPendapatan != null
                && totalBeban != null
                && labaSebelumPajak != null) {

            labaSebelumPajak.setBulanIni(
                    totalPendapatan.getBulanIni()
                            .subtract(totalBeban.getBulanIni())
            );

            labaSebelumPajak.setTahunBerjalan(
                    totalPendapatan.getTahunBerjalan()
                            .subtract(totalBeban.getTahunBerjalan())
            );
        }

        // 7. Susun hierarki parent dan children
        List<LaporanLabaRugiResponse> result =
                new ArrayList<>();

        for (LaporanKeuanganPos pos : positions) {

            LaporanLabaRugiResponse current =
                    responseMap.get(pos.getId());

            if (pos.getParentId() == null) {
                result.add(current);
            } else {
                LaporanLabaRugiResponse parent =
                        responseMap.get(pos.getParentId());

                if (parent != null) {
                    parent.getChildren().add(current);
                }
            }
        }

        return result;
    }

    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private LaporanLabaRugiResponse findByKode(
            Map<Long, LaporanLabaRugiResponse> responseMap,
            List<LaporanKeuanganPos> positions,
            String kodePos) {

        return positions.stream()
                .filter(pos -> kodePos.equals(pos.getKodePos()))
                .map(pos -> responseMap.get(pos.getId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<LaporanLabaRugiDetailResponse> getDetailJurnal(
            String kodePos,
            YearMonth periode,
            Long cabangId) {

        LocalDateTime awalPeriode = periode
                .atDay(1)
                .atStartOfDay();

        LocalDateTime akhirPeriode = periode
                .plusMonths(1)
                .atDay(1)
                .atStartOfDay();

        List<LaporanLabaRugiDetailProjection> details =
                laporanRepository.findDetailJurnal(
                        kodePos,
                        awalPeriode,
                        akhirPeriode,
                        cabangId
                );

        return details.stream().map(item -> {

            LaporanLabaRugiDetailResponse response =
                    new LaporanLabaRugiDetailResponse();

            response.setTanggal(item.getTanggal());
            response.setNomorJurnal(item.getNomorJurnal());
            response.setNomorTransaksi(item.getNomorTransaksi());
            response.setNomorGl(item.getNomorGl());
            response.setNamaGlAccount(item.getNamaGlAccount());
            response.setDk(item.getDk());
            response.setDebit(safe(item.getDebit()));
            response.setKredit(safe(item.getKredit()));
            response.setSaldo(safe(item.getSaldo()));

            return response;

        }).toList();
    }
}