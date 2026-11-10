package com.project.gl.repo;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.gl.model.entity.GlDailyClosing;

public interface GlDailyClosingRepository extends JpaRepository<GlDailyClosing, Long> {

    Optional<GlDailyClosing> findByTanggalAndCabangId(
            LocalDate tanggal,
            Integer cabangId
    );

    boolean existsByTanggalAndCabangIdAndStatus(
            LocalDate tanggal,
            Integer cabangId,
            String status
    );
}