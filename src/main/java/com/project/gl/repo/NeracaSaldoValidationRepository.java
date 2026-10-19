package com.project.gl.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.gl.model.entity.NeracaSaldoValidation;

@Repository
public interface NeracaSaldoValidationRepository extends JpaRepository<NeracaSaldoValidation, Long> {

    List<NeracaSaldoValidation> findTop3ByOrderByValidatedAtDesc();

    List<NeracaSaldoValidation>
        findBySubmissionIdOrderByValidatedAtDesc(
            Long submissionId
        );
}