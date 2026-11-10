package com.project.gl.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.gl.model.entity.OjkRatioReport;
import com.project.gl.model.enums.OjkRatioStatus;

@Repository
public interface OjkRatioReportRepository extends JpaRepository<OjkRatioReport, Long> {

    Optional<OjkRatioReport> findByPeriode(LocalDate periode);

    Optional<OjkRatioReport> findByReportNo(String reportNo);

    List<OjkRatioReport> findAllByOrderByPeriodeDesc();

    Page<OjkRatioReport> findByStatusOrderByPeriodeDesc(
            OjkRatioStatus status,
            Pageable pageable
    );

    boolean existsByPeriode(LocalDate periode);
}