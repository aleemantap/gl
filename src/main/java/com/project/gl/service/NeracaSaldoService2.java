package com.project.gl.service;

import java.time.LocalDate;
import java.util.List;

import com.project.gl.model.entity.NeracaSaldoSubmission;
import com.project.gl.model.entity.NeracaSaldoValidation;
import com.project.gl.model.enums.NeracaSaldoStatus;
import com.project.gl.model.response.NeracaSaldoRow;
import com.project.gl.model.response.NeracaSaldoSubmissionRow;
import com.project.gl.model.response.NeracaSaldoValidationRow;

public interface NeracaSaldoService2 {

      List<NeracaSaldoRow> getNeracaSaldo(
            Long unitId,
            LocalDate periode
      );    

    NeracaSaldoSubmission submit(
            LocalDate periode,
            Long unitId,
            String submittedBy
    );

    NeracaSaldoSubmission validate(
            Long submissionId,
            String validatedBy,
            String note
    );

    NeracaSaldoSubmission reject(
            Long submissionId,
            String validatedBy,
            String note
    );

    NeracaSaldoSubmission findById(
            Long id
    );

    List<NeracaSaldoSubmission> getQueue(
            LocalDate periode
    );

    List<NeracaSaldoValidation> getRecentValidations();

    List<NeracaSaldoValidation> getValidationHistory(
            Long submissionId
    );

    long countWaiting(
            LocalDate periode
    );

    long countTotal(
            LocalDate periode
    );

    long countValid(
            LocalDate periode
    );

    List<NeracaSaldoSubmissionRow> getQueueWithCabang(LocalDate periode);

    List<NeracaSaldoValidationRow> getRecentValidationRows();
}