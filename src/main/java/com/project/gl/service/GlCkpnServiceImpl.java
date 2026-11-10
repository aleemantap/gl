package com.project.gl.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.gl.config.PencadanganApiClient;
import com.project.gl.model.response.GlCkpnPreviewResponse;
import com.project.gl.model.response.PencadanganApiResponse;
import com.project.gl.repo.JournalDetailRepository;
import com.project.gl.repo.JournalRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GlCkpnServiceImpl implements GlCkpnService {

    private final PencadanganApiClient pencadanganApiClient;
    private final JournalRepository journalRepository;

    @Value("${gl.ckpn.cadangan-account-id}")
    private Integer cadanganAccountId;

    @Override
    @Transactional(readOnly = true)
    public GlCkpnPreviewResponse preview(
            YearMonth periode,
            Integer cabangId,
            String kodeCabang) {

        if (periode == null) {
            throw new IllegalArgumentException("Periode wajib diisi");
        }

        if (cabangId == null) {
            throw new IllegalArgumentException("Cabang wajib dipilih");
        }

        if (kodeCabang == null || kodeCabang.isBlank()) {
            throw new IllegalArgumentException("Kode cabang wajib diisi");
        }

        // 1. Ambil data dari API Gadai
        PencadanganApiResponse apiResponse =
                pencadanganApiClient.getPencadangan(
                        periode.toString(),
                        kodeCabang
                );

        PencadanganApiResponse.Data data = apiResponse.getData();

        if (data == null) {
            throw new IllegalStateException(
                    "Data pencadangan dari API Gadai kosong"
            );
        }

        // 2. Validasi periode dan cabang
        if (!periode.toString().equals(data.getPeriode())) {
            throw new IllegalStateException(
                    "Periode dari API Gadai tidak sesuai"
            );
        }

        if (!kodeCabang.equals(data.getKodeCabang())) {
            throw new IllegalStateException(
                    "Kode cabang dari API Gadai tidak sesuai"
            );
        }

        // 3. Validasi tanggal posisi
        LocalDate tanggalPosisi;

        try {
            tanggalPosisi = LocalDate.parse(data.getTanggalPosisi());
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Format tanggal posisi dari API Gadai tidak valid"
            );
        }

        if (!tanggalPosisi.equals(periode.atEndOfMonth())) {
            throw new IllegalStateException(
                    "Tanggal posisi API Gadai tidak sesuai akhir periode"
            );
        }

        // 4. Validasi nominal
        BigDecimal totalCadanganGadai = data.getTotalCadangan();

        if (totalCadanganGadai == null
                || totalCadanganGadai.signum() < 0) {
            throw new IllegalStateException(
                    "Total cadangan Gadai tidak valid"
            );
        }

        // 5. Ambil saldo GL sampai akhir periode
        LocalDateTime akhirPeriode = periode
                .plusMonths(1)
                .atDay(1)
                .atStartOfDay();

        BigDecimal saldoCadanganGl =
                journalRepository.findSaldoCadanganKerugian(
                        cadanganAccountId,
                        cabangId,
                        akhirPeriode
                );

        if (saldoCadanganGl == null) {
            saldoCadanganGl = BigDecimal.ZERO;
        }

        // 6. Hitung selisih
        BigDecimal selisih =
                totalCadanganGadai.subtract(saldoCadanganGl);

        BigDecimal nominalJurnal = selisih.abs();

        // 7. Tentukan arah jurnal
        String arahJurnal;

        if (selisih.signum() > 0) {
            arahJurnal = "DEBIT_BEBAN_KREDIT_CADANGAN";
        } else if (selisih.signum() < 0) {
            arahJurnal = "DEBIT_CADANGAN_KREDIT_BEBAN";
        } else {
            arahJurnal = "TIDAK_ADA_JURNAL";
        }

        boolean jurnalDiperlukan = selisih.compareTo(BigDecimal.ZERO) != 0;

        // 8. Return preview
        return GlCkpnPreviewResponse.builder()
                .periode(periode.toString())
                .tanggalPosisi(tanggalPosisi)
                .cabangId(cabangId)
                .kodeCabang(data.getKodeCabang())
                .namaCabang(data.getNamaCabang())
                .referensiLaporan(data.getReferensiLaporan())
                .totalCadanganGadai(totalCadanganGadai)
                .saldoCadanganGl(saldoCadanganGl)
                .selisih(selisih)
                .nominalJurnal(nominalJurnal)
                .arahJurnal(arahJurnal)
                .jurnalDiperlukan(jurnalDiperlukan)
                .status(jurnalDiperlukan
                        ? "PERLU_PENYESUAIAN"
                        : "SEIMBANG")
                .build();
    }
}