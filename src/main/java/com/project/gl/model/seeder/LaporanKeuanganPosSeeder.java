package com.project.gl.model.seeder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import com.project.gl.model.entity.JenisLaporanKeuangan;
import com.project.gl.model.entity.LaporanKeuanganPos;
import com.project.gl.repo.LaporanKeuanganPosRepository;

@Configuration
@RequiredArgsConstructor
public class LaporanKeuanganPosSeeder {

    private final LaporanKeuanganPosRepository posRepository;

    @Bean
    ApplicationRunner seedLaporanKeuanganPos() {
        return args -> seed();
    }

    @Transactional
    public void seed() {

        JenisLaporanKeuangan jenis = JenisLaporanKeuangan.LABA_RUGI;

        // =====================================
        // 1. PENDAPATAN OPERASIONAL
        // =====================================

        LaporanKeuanganPos pendapatan = savePos(
            "LR-01",
            "Pendapatan Operasional",
            jenis,
            null,
            1,
            false
        );

        savePos(
            "LR-01-01",
            "Pendapatan Sewa Modal / Bunga",
            jenis,
            pendapatan.getId(),
            1,
            false
        );

        savePos(
            "LR-01-02",
            "Pendapatan Administrasi",
            jenis,
            pendapatan.getId(),
            2,
            false
        );

        savePos(
            "LR-01-03",
            "Pendapatan Provisi",
            jenis,
            pendapatan.getId(),
            3,
            false
        );

        savePos(
            "LR-01-04",
            "Pendapatan Margin Emas (Investasi)",
            jenis,
            pendapatan.getId(),
            4,
            false
        );

        savePos(
            "LR-01-T",
            "Jumlah Pendapatan",
            jenis,
            pendapatan.getId(),
            5,
            true
        );

        // =====================================
        // 2. BEBAN OPERASIONAL
        // =====================================

        LaporanKeuanganPos beban = savePos(
            "LR-02",
            "Beban Operasional",
            jenis,
            null,
            2,
            false
        );

        savePos(
            "LR-02-01",
            "Beban Operasional Kas Kecil",
            jenis,
            beban.getId(),
            1,
            false
        );

        savePos(
            "LR-02-02",
            "Beban Cadangan Penyisihan Piutang (CKPN)",
            jenis,
            beban.getId(),
            2,
            false
        );

        savePos(
            "LR-02-T",
            "Jumlah Beban",
            jenis,
            beban.getId(),
            3,
            true
        );

        // =====================================
        // 3. LABA SEBELUM PAJAK
        // =====================================

        savePos(
            "LR-03",
            "Laba Sebelum Pajak",
            jenis,
            null,
            3,
            true
        );
    }

    private LaporanKeuanganPos savePos(
        String kodePos,
        String namaPos,
        JenisLaporanKeuangan jenis,
        Long parentId,
        Integer urutan,
        Boolean isTotal
    ) {

        LaporanKeuanganPos pos = posRepository
            .findByKodePosAndJenisLaporan(kodePos, jenis)
            .orElseGet(LaporanKeuanganPos::new);

        pos.setKodePos(kodePos);
        pos.setNamaPos(namaPos);
        pos.setJenisLaporan(jenis);
        pos.setParentId(parentId);
        pos.setUrutan(urutan);
        pos.setIsTotal(isTotal);
        pos.setIsActive(true);

        return posRepository.save(pos);
    }
}