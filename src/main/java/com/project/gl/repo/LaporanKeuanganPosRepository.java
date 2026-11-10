package com.project.gl.repo;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.gl.model.entity.JenisLaporanKeuangan;
import com.project.gl.model.entity.LaporanKeuanganPos;

import java.util.List;
import java.util.Optional;

public interface LaporanKeuanganPosRepository
        extends JpaRepository<LaporanKeuanganPos, Long> {

    List<LaporanKeuanganPos>
        findByJenisLaporanAndIsActiveTrueOrderByUrutanAsc(
            JenisLaporanKeuangan jenisLaporan
        );

    List<LaporanKeuanganPos>
        findByJenisLaporanAndParentIdIsNullAndIsActiveTrueOrderByUrutanAsc(
            JenisLaporanKeuangan jenisLaporan
        );

    boolean existsByKodePosAndJenisLaporan(
        String kodePos,
        JenisLaporanKeuangan jenisLaporan
    );

    Optional<LaporanKeuanganPos> findByKodePosAndJenisLaporan(
        String kodePos,
        JenisLaporanKeuangan jenisLaporan
    );
}