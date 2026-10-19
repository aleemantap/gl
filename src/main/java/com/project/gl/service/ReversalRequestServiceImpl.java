package com.project.gl.service;

import com.project.gl.model.entity.GlCashMovement;
import com.project.gl.model.entity.JournalDetail;
import com.project.gl.model.entity.JournalHeader;
import com.project.gl.model.entity.ReversalRequest;
import com.project.gl.model.enums.ReversalRequestStatus;
import com.project.gl.model.response.CashMovementRow;
import com.project.gl.model.response.JournalDetailRow;
import com.project.gl.model.response.ReversalRequestDetail;
import com.project.gl.model.response.ReversalRequestRow;
import com.project.gl.repo.JournalHeaderRepository;
import com.project.gl.repo.ReversalRequestRepository;
import com.project.gl.repo.GlCashMovementRepository;
import com.project.gl.repo.JournalDetailRepository;
import com.project.gl.service.ReversalRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReversalRequestServiceImpl implements ReversalRequestService {

    private final ReversalRequestRepository reversalRequestRepository;
    private final JournalHeaderRepository journalHeaderRepository;
    private final JournalDetailRepository journalDetailRepository;
    private final GlCashMovementRepository cashMovementRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ReversalRequest> findAll() {

        return reversalRequestRepository
                .findAll()
                .stream()
                .sorted(
                    (a, b) -> b.getRequestedAt()
                            .compareTo(a.getRequestedAt())
                )
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<ReversalRequest> findByStatus(
            ReversalRequestStatus status) {

        return reversalRequestRepository
                .findByStatusOrderByRequestedAtDesc(status);
    }


    @Override
    @Transactional(readOnly = true)
    public ReversalRequest findById(Long id) {

        return reversalRequestRepository
                .findById(id)
                .orElseThrow(() ->
                    new RuntimeException(
                        "Reversal request tidak ditemukan dengan ID: " + id
                    )
                );
    }


    @Override
    @Transactional
    public ReversalRequest createRequest(
            Long originalJournalId,
            String requestedBy,
            String reason,
            String description) {

        JournalHeader original =
                journalHeaderRepository.findById(
                    originalJournalId
                ).orElseThrow(() ->
                    new IllegalStateException(
                        "Jurnal tidak ditemukan dengan ID: "
                        + originalJournalId
                    )
                );

        validateOriginalJournal(original);

        boolean waitingExists =
                reversalRequestRepository
                    .existsByOriginalJournalIdAndStatus(
                        originalJournalId,
                        ReversalRequestStatus.MENUNGGU
                    );

        if (waitingExists) {
            throw new IllegalStateException(
                "Permintaan reversal untuk jurnal "
                + original.getJournalNo()
                + " sedang menunggu persetujuan."
            );
        }

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException(
                "Alasan pembalikan wajib diisi."
            );
        }

        ReversalRequest request =
                new ReversalRequest();

        request.setOriginalJournalId(
            originalJournalId
        );

        request.setRequestedBy(
            requestedBy
        );

        request.setRequestedAt(
            LocalDateTime.now()
        );

        request.setReason(
            reason
        );

        request.setDescription(
            description
        );

        request.setStatus(
            ReversalRequestStatus.MENUNGGU
        );

        return reversalRequestRepository.save(
            request
        );
    }
   

    @Override
    @Transactional
    public void approve(Long id, String approvedBy) {

        // 1. Pastikan request masih MENUNGGU
        ReversalRequest request = validateWaiting(id);

        // 2. Ambil jurnal asli
        JournalHeader original =
                journalHeaderRepository.findById(request.getOriginalJournalId())
                    .orElseThrow(() ->
                        new IllegalStateException(
                            "Jurnal asli tidak ditemukan dengan ID: "
                            + request.getOriginalJournalId()
                        )
                    );

        // 3. Validasi jurnal asli
        validateOriginalJournal(original);

        // 4. Ambil detail jurnal asli
        List<JournalDetail> originalDetails =
                journalDetailRepository
                    .findByJournalHeaderId(original.getId());

        if (originalDetails == null || originalDetails.isEmpty()) {
            throw new IllegalStateException(
                "Jurnal asli tidak memiliki detail."
            );
        }

        // 5. Pastikan jurnal asli balance
        validateJournalBalance(originalDetails);

        // 6. Buat Journal Header reversal
        JournalHeader reversal =
                createReversalHeader(original);

        // 7. Copy detail + balik D/K
        createReversalDetails(
            original,
            reversal,
            originalDetails
        );

        // 8. Copy cash movement + balik IN/OUT
        createReversalCashMovements(
            original,
            reversal
        );

        // 9. Update request menjadi DISETUJUI
        request.setStatus(ReversalRequestStatus.DISETUJUI);
        request.setApprovedBy(approvedBy);
        request.setApprovedAt(LocalDateTime.now());

        reversalRequestRepository.save(request);
    }

    @Override
    @Transactional
    public void reject(
            Long id,
            String rejectedBy,
            String rejectionReason) {

        ReversalRequest request =
                validateWaiting(id);

        if (rejectionReason == null
                || rejectionReason.isBlank()) {

            throw new IllegalArgumentException(
                "Alasan penolakan wajib diisi."
            );
        }

        request.setStatus(
            ReversalRequestStatus.DITOLAK
        );

        request.setRejectedBy(
            rejectedBy
        );

        request.setRejectedAt(
            LocalDateTime.now()
        );

        request.setRejectionReason(
            rejectionReason
        );

        reversalRequestRepository.save(request);
    }
    

    private void validateWaiting(
            ReversalRequest request) {

        if (request.getStatus()
                != ReversalRequestStatus.MENUNGGU) {

            throw new IllegalStateException(
                "Permintaan pembalikan sudah diproses."
            );
        }
    }
    @Override
    @Transactional(readOnly = true)
    public List<ReversalRequestRow> findAllRows() {

        return reversalRequestRepository.findAllRows();
    }

    private ReversalRequest validateWaiting(Long id) {

        ReversalRequest request = reversalRequestRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Permintaan pembalikan tidak ditemukan dengan ID: " + id
                        )
                );

        if (request.getStatus() != ReversalRequestStatus.MENUNGGU) {
            throw new IllegalStateException(
                    "Permintaan pembalikan sudah diproses. " +
                    "Status saat ini: " + request.getStatus()
            );
        }

        return request;
    }

    private void createReversalCashMovements(JournalHeader original,JournalHeader reversal) {

        List<GlCashMovement> originalMovements =
                cashMovementRepository
                    .findByJournalHeaderId(
                        original.getId()
                    );

        for (GlCashMovement originalMovement  : originalMovements) {
               

            GlCashMovement reversalMovement =  new GlCashMovement();
                   

            reversalMovement.setJournalHeaderId(
                reversal.getId()
            );

            String originalDirection =   originalMovement.getDirection();

                   
            if ("IN".equalsIgnoreCase(originalDirection)) {

                reversalMovement.setDirection("OUT");

            } else if ("OUT".equalsIgnoreCase(originalDirection)) {

                reversalMovement.setDirection("IN");

            } else {

                throw new IllegalStateException(
                    "Direction Cash Movement tidak valid: "
                    + originalDirection
                    + " pada ID: "
                    + originalMovement.getId()
                );
            }

            reversalMovement.setAmount(
                originalMovement.getAmount()
            );

            reversalMovement.setTellerUserId(
                originalMovement.getTellerUserId()
            );

            reversalMovement.setKasPointCode(
                originalMovement.getKasPointCode()
            );

            reversalMovement.setCounterpartyType(
                originalMovement.getCounterpartyType()
            );

            reversalMovement.setCounterpartyRef(
                originalMovement.getCounterpartyRef()
            );

            reversalMovement.setCounterpartyName(
                originalMovement.getCounterpartyName()
            );

            reversalMovement.setOccurredAt(
                LocalDateTime.now()
            );

            reversalMovement.setDescription(
                "Reversal dari "
                + original.getJournalNo()
            );

            cashMovementRepository.save(
                reversalMovement
            );
        }
    }
   
    private JournalHeader createReversalHeader(JournalHeader original) {

        JournalHeader reversal = new JournalHeader();

        reversal.setJournalId(original.getJournalId());
        reversal.setTransactionCodeId(original.getTransactionCodeId());
        reversal.setCabangId(original.getCabangId());

        reversal.setTransactionNo(
            original.getTransactionNo()
        );

        reversal.setTransactionDate(
            LocalDateTime.now()
        );

        reversal.setStatus("POSTED");

        /*
        * Deterministic idempotency key.
        *
        * Satu jurnal asli hanya boleh mempunyai
        * satu reversal.
        */
        reversal.setIdempotencyKey(
            "REVERSAL-" + original.getId()
        );

        /*
        * Ini yang menunjukkan bahwa journal baru
        * merupakan reversal dari journal asli.
        */
        reversal.setReversalOfJournalId(
            original.getId()
        );

        reversal.setCreatedAt(
            LocalDateTime.now()
        );

        JournalHeader saved =
                journalHeaderRepository.save(reversal);

        /*
        * Format journal number mengikuti mekanisme
        * JournalService yang sudah berjalan.
        */
        String branchCode = "-";

        if (original.getCabang() != null) {
            branchCode =
                original.getCabang().getKodeCabang();
        }

        String journalNo = String.format(
            "%s/JV/%tY/%<tm/%06d",
            branchCode,
            LocalDateTime.now(),
            saved.getId()
        );

        saved.setJournalNo(journalNo);

        return journalHeaderRepository.save(saved);
    }
    
   
    private void validateJournalBalance(List<JournalDetail> details) {

        BigDecimal totalDebit =
                details.stream()
                    .filter(d ->
                        "D".equalsIgnoreCase(d.getDk())
                    )
                    .map(JournalDetail::getAmount)
                    .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                    );

        BigDecimal totalCredit =
                details.stream()
                    .filter(d ->
                        "K".equalsIgnoreCase(d.getDk())
                    )
                    .map(JournalDetail::getAmount)
                    .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                    );

        if (totalDebit.compareTo(totalCredit) != 0) {

            throw new IllegalStateException(
                "Jurnal asli tidak balance. "
                + "Debit: " + totalDebit
                + ", Kredit: " + totalCredit
            );
        }
    }

    private void validateOriginalJournal(JournalHeader original) {

        // Jangan izinkan reversal terhadap jurnal reversal
        if (original.getReversalOfJournalId() != null) {
            throw new IllegalStateException(
                "Jurnal ini merupakan jurnal reversal dan tidak dapat direversal kembali."
            );
        }

        // Jangan izinkan reversal kedua terhadap jurnal yang sama
        if (journalHeaderRepository
                .existsByReversalOfJournalId(original.getId())) {

            throw new IllegalStateException(
                "Jurnal " + original.getJournalNo()
                + " sudah memiliki jurnal reversal."
            );
        }

        // Jurnal harus sudah POSTED
        if (!"POSTED".equalsIgnoreCase(original.getStatus())) {
            throw new IllegalStateException(
                "Jurnal dengan nomor "
                + original.getJournalNo()
                + " belum berstatus POSTED."
            );
        }
    }


    private void createReversalDetails(JournalHeader original,JournalHeader reversal,List<JournalDetail> originalDetails) {

        for (JournalDetail originalDetail : originalDetails) {

            JournalDetail reversalDetail =
                    new JournalDetail();

            reversalDetail.setJournalHeaderId(
                reversal.getId()
            );

            reversalDetail.setGlAccountId(
                originalDetail.getGlAccountId()
            );

            String originalDk =
                    originalDetail.getDk();

            if ("D".equalsIgnoreCase(originalDk)) {

                reversalDetail.setDk("K");

            } else if ("K".equalsIgnoreCase(originalDk)) {

                reversalDetail.setDk("D");

            } else {

                throw new IllegalStateException(
                    "DK tidak valid pada Journal Detail ID: "
                    + originalDetail.getId()
                );
            }

            reversalDetail.setAmount(
                originalDetail.getAmount()
            );

            reversalDetail.setDescription(
                "Reversal dari "
                + original.getJournalNo()
            );

            reversalDetail.setCreatedAt(
                LocalDateTime.now()
            );

            journalDetailRepository.save(
                reversalDetail
            );
        }
    }


    @Override
    @Transactional(readOnly = true)
    public ReversalRequestDetail findDetail(Long id) {

        ReversalRequest request =
                reversalRequestRepository.findById(id)
                    .orElseThrow(() ->
                        new IllegalStateException(
                            "Permintaan pembalikan tidak ditemukan dengan ID: "
                            + id
                        )
                    );

        JournalHeader journal =
                journalHeaderRepository.findById(
                    request.getOriginalJournalId()
                ).orElseThrow(() ->
                    new IllegalStateException(
                        "Jurnal asli tidak ditemukan dengan ID: "
                        + request.getOriginalJournalId()
                    )
                );

        List<JournalDetail> details =
                journalDetailRepository
                    .findByJournalHeaderId(
                        journal.getId()
                    );

        List<JournalDetailRow> detailRows =
        details.stream()
            .map(d -> {

                String nomorGl = "-";
                String namaGlAccount = "-";

                if (d.getNomorGlAccount() != null) {
                    nomorGl =
                        d.getNomorGlAccount().getNomorGl();

                    namaGlAccount =
                        d.getNomorGlAccount()
                            .getNamaGlAccount();
                }

                return new JournalDetailRow(
                    d.getId(),
                    d.getGlAccountId(),
                    nomorGl,
                    namaGlAccount,
                    d.getDk(),
                    d.getAmount(),
                    d.getDescription()
                );
            })
            .toList();

        BigDecimal totalDebit =
                details.stream()
                    .filter(d ->
                        "D".equalsIgnoreCase(d.getDk())
                    )
                    .map(JournalDetail::getAmount)
                    .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                    );

        BigDecimal totalCredit =
                details.stream()
                    .filter(d ->
                        "K".equalsIgnoreCase(d.getDk())
                    )
                    .map(JournalDetail::getAmount)
                    .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                    );

        List<GlCashMovement> movements =
                cashMovementRepository
                    .findByJournalHeaderId(
                        journal.getId()
                    );

        List<CashMovementRow> cashRows =
                movements.stream()
                    .map(m ->
                        new CashMovementRow(
                            m.getId(),
                            m.getDirection(),
                            m.getAmount(),
                            m.getKasPointCode(),
                            m.getCounterpartyName(),
                            m.getDescription()
                        )
                    )
                    .toList();

        String cabangCode = "-";
        String cabangName = "-";

        if (journal.getCabang() != null) {
            cabangCode =
                    journal.getCabang().getKodeCabang();

            cabangName =
                    journal.getCabang().getNamaCabang();
        }

        return new ReversalRequestDetail(
            request.getId(),
            request.getOriginalJournalId(),
            journal.getJournalNo(),
            journal.getTransactionNo(),
            cabangCode,
            cabangName,
            request.getRequestedBy(),
            request.getRequestedAt()
                    .format(
                        java.time.format.DateTimeFormatter
                            .ofPattern("dd-MM-yyyy HH:mm")
                    ),
            request.getReason(),
            request.getDescription(),
            request.getStatus().name(),
            totalDebit,
            totalCredit,
            detailRows,
            cashRows
        );
    }
}