package com.project.gl.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.gl.model.entity.NeracaSaldoSubmission;
import com.project.gl.model.enums.NeracaSaldoStatus;

@Repository
public interface NeracaSaldoSubmissionRepository extends JpaRepository<NeracaSaldoSubmission, Long> {

    Optional<NeracaSaldoSubmission> findByPeriodeAndUnitId(
            LocalDate periode,
            Long unitId
    );

    List<NeracaSaldoSubmission> findByPeriodeOrderBySubmittedAtDesc(
            LocalDate periode
    );

    List<NeracaSaldoSubmission> findByPeriodeAndStatusOrderBySubmittedAtAsc(
            LocalDate periode,
            NeracaSaldoStatus status
    );

    long countByPeriodeAndStatus(
            LocalDate periode,
            NeracaSaldoStatus status
    );

    long countByPeriode(
            LocalDate periode
    );
}