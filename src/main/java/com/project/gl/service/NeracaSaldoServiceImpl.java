package com.project.gl.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.NeracaSaldoSubmission;
import com.project.gl.model.entity.NeracaSaldoValidation;
import com.project.gl.model.enums.NeracaSaldoStatus;
import com.project.gl.model.response.NeracaSaldoRow;
import com.project.gl.model.response.NeracaSaldoSubmissionRow;
import com.project.gl.model.response.NeracaSaldoValidationRow;
import com.project.gl.repo.CabangRepository;
import com.project.gl.repo.NeracaSaldoReportRepository;
import com.project.gl.repo.NeracaSaldoSubmissionRepository;
import com.project.gl.repo.NeracaSaldoValidationReportRepository;
import com.project.gl.repo.NeracaSaldoValidationRepository;
import com.project.gl.service.NeracaSaldoService2;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NeracaSaldoServiceImpl implements NeracaSaldoService2 {

        private final NeracaSaldoSubmissionRepository submissionRepository;
        private final NeracaSaldoValidationRepository validationRepository;
        private final NeracaSaldoReportRepository neracaSaldoReportRepository;
        private final CabangRepository cabangRepository;
        private final NeracaSaldoValidationReportRepository validationReportRepository;

        @Override
        @Transactional(readOnly = true)
        public List<NeracaSaldoRow> getNeracaSaldo(Long unitId, LocalDate periode) {

                LocalDate startDate = periode.withDayOfMonth(1);
                LocalDate endDate = startDate.plusMonths(1);
                return neracaSaldoReportRepository.getNeracaSaldo(unitId,startDate,endDate);
        }


        @Override
        public NeracaSaldoSubmission submit(
                LocalDate periode,
                Long unitId,
                String submittedBy) {

                NeracaSaldoSubmission submission = submissionRepository
                        .findByPeriodeAndUnitId(periode, unitId)
                        .orElse(null);

                /*
                * Belum pernah submit
                */
                if (submission == null) {

                submission = new NeracaSaldoSubmission();

                submission.setPeriode(periode);
                submission.setUnitId(unitId);
                }

                /*
                * Submit ulang setelah ditolak
                */
                submission.setStatus(
                        NeracaSaldoStatus.MENUNGGU
                );

                submission.setSubmittedBy(
                        submittedBy
                );

                submission.setSubmittedAt(
                        LocalDateTime.now()
                );

                /*
                * Reset informasi validasi sebelumnya
                */
                submission.setValidatedBy(null);
                submission.setValidatedAt(null);
                submission.setValidationNote(null);
                return submissionRepository.save(submission);
        }


        @Override
        public NeracaSaldoSubmission validate(
                Long submissionId,
                String validatedBy,
                String note) {

                NeracaSaldoSubmission submission = getRequiredSubmission(submissionId);
                
                if (submission.getStatus() != NeracaSaldoStatus.MENUNGGU) {
                        throw new IllegalStateException(
                        "Neraca Saldo sudah tidak berada dalam status MENUNGGU."
                        );
                }

                List<NeracaSaldoRow> rows =
                        getNeracaSaldo(
                                submission.getUnitId(),
                                submission.getPeriode()
                        );

                BigDecimal totalDebit = rows.stream()
                        .map(NeracaSaldoRow::getDebit)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalCredit = rows.stream()
                        .map(NeracaSaldoRow::getCredit)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (totalDebit.compareTo(totalCredit) != 0) {
                        throw new IllegalStateException(
                                "Neraca Saldo tidak balance. " +
                                "Debit: " + totalDebit +
                                ", Kredit: " + totalCredit
                        );
                }

             

                submission.setStatus(NeracaSaldoStatus.VALID);
                submission.setValidatedBy(validatedBy);
                submission.setValidatedAt(LocalDateTime.now());
                submission.setValidationNote(note);
                submissionRepository.save(submission);

                saveHistory(
                        submission,
                        NeracaSaldoStatus.VALID,
                        validatedBy,
                        note
                );

                return submission;
        }


        @Override
        public NeracaSaldoSubmission reject(
                Long submissionId,
                String validatedBy,
                String note) {

                NeracaSaldoSubmission submission =  getRequiredSubmission(submissionId);

                if (submission.getStatus() != NeracaSaldoStatus.MENUNGGU) {
                        throw new IllegalStateException("Neraca Saldo sudah tidak berada dalam status MENUNGGU.");
                }
                submission.setStatus(NeracaSaldoStatus.DITOLAK);
                submission.setValidatedBy(validatedBy);
                submission.setValidatedAt(LocalDateTime.now());
                submission.setValidationNote(note);
                submissionRepository.save(submission);

                saveHistory(
                        submission,
                        NeracaSaldoStatus.DITOLAK,
                        validatedBy,
                        note
                );

                return submission;
        }


        private void saveHistory(
                NeracaSaldoSubmission submission,
                NeracaSaldoStatus status,
                String validatedBy,
                String note) {

                NeracaSaldoValidation history =
                        new NeracaSaldoValidation();

                history.setSubmissionId(
                        submission.getId()
                );

                history.setStatus(
                        status
                );

                history.setValidatedBy(
                        validatedBy
                );

                history.setValidatedAt(
                        LocalDateTime.now()
                );

                history.setNote(
                        note
                );

                validationRepository.save(history);
        }


        @Override
        @Transactional(readOnly = true)
        public NeracaSaldoSubmission findById(
                Long id) {

                return getRequiredSubmission(id);
        }


        @Override
        @Transactional(readOnly = true)
        public List<NeracaSaldoSubmission> getQueue(
                LocalDate periode) {

                return submissionRepository
                        .findByPeriodeOrderBySubmittedAtDesc(
                                periode
                        );
        }


        @Override
        @Transactional(readOnly = true)
        public List<NeracaSaldoValidation>
                getRecentValidations() {

                return validationRepository
                        .findTop3ByOrderByValidatedAtDesc();
        }


        @Override
        @Transactional(readOnly = true)
        public List<NeracaSaldoValidation>
                getValidationHistory(
                        Long submissionId) {

                return validationRepository
                        .findBySubmissionIdOrderByValidatedAtDesc(
                                submissionId
                        );
        }


        @Override
        @Transactional(readOnly = true)
        public long countWaiting(
                LocalDate periode) {

                return submissionRepository
                        .countByPeriodeAndStatus(
                        periode,
                        NeracaSaldoStatus.MENUNGGU
                        );
        }


        @Override
        @Transactional(readOnly = true)
        public long countTotal(
                LocalDate periode) {

                return submissionRepository
                        .countByPeriode(periode);
        }


        @Override
        @Transactional(readOnly = true)
        public long countValid(
                LocalDate periode) {

                return submissionRepository
                        .countByPeriodeAndStatus(
                        periode,
                        NeracaSaldoStatus.VALID
                        );
        }


        private NeracaSaldoSubmission getRequiredSubmission(
                Long id) {

                return submissionRepository
                        .findById(id)
                        .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Neraca Saldo Submission tidak ditemukan. ID: "
                                + id
                        )
                        );
        }

        @Override
        @Transactional(readOnly = true)
        public List<NeracaSaldoSubmissionRow> getQueueWithCabang(LocalDate periode) {
        
                return submissionRepository
                .findByPeriodeOrderBySubmittedAtDesc(periode)
                .stream()
                .map(submission -> {

                        Cabang cabang = cabangRepository
                                .findById(submission.getUnitId().intValue())
                                .orElse(null);

                        return new NeracaSaldoSubmissionRow(
                                submission.getId(),
                                submission.getPeriode(),
                                submission.getUnitId(),
                                cabang != null
                                        ? cabang.getKodeCabang()
                                        : "-",
                                cabang != null
                                        ? cabang.getNamaCabang()
                                        : "Cabang tidak ditemukan",
                                submission.getStatus(),
                                submission.getSubmittedBy(),
                                submission.getSubmittedAt()
                        );
                })
                .toList();
        }


        @Override
        @Transactional(readOnly = true)
        public List<NeracaSaldoValidationRow> getRecentValidationRows() {
                 return validationReportRepository.findRecentValidations(3);
        }
}