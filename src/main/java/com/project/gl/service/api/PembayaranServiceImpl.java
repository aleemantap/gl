package com.project.gl.service.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.gl.exception.JournalValidationException;
import com.project.gl.exception.ResourceNotFoundException;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.GlCashMovement;
import com.project.gl.model.entity.JournalDetail;
import com.project.gl.model.entity.JournalHeader;
import com.project.gl.model.entity.JournalId;
import com.project.gl.model.entity.JournalLineTemplate;
import com.project.gl.model.entity.KodeTransaksi;
import com.project.gl.model.entity.NomorGlAccount;
import com.project.gl.model.entity.Product;
import com.project.gl.model.entity.ProductRule;
import com.project.gl.model.entity.ProductRuleComponent;
import com.project.gl.model.entity.TransactionCodeJournal;
import com.project.gl.model.enums.TransactionRejectionStatus;
import com.project.gl.model.request.JournalRequest;
import com.project.gl.model.response.JournalResponse;
import com.project.gl.model.response.PreparedJournalLineDto;
import com.project.gl.repo.CabangRepository;
import com.project.gl.repo.GlCashMovementRepository;
import com.project.gl.repo.JournalDetailRepository;
import com.project.gl.repo.JournalHeaderRepository;
import com.project.gl.repo.JournalLineTemplateRepository;
import com.project.gl.repo.KodeTransaksiRepository;
import com.project.gl.repo.ProductRepository;
import com.project.gl.repo.ProductRuleComponentRepository;
import com.project.gl.repo.ProductRuleRepository;
import com.project.gl.repo.TransactionJournalRepository;
import com.project.gl.repo.NomorGlAccountRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.project.gl.model.entity.NomorGlAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.gl.model.entity.TransactionCollateral;
import com.project.gl.repo.TransactionCollateralRepository;
import com.project.gl.service.TransactionRejectionService;

@Service
public class PembayaranServiceImpl implements PembayaranService {

    @Autowired
    private KodeTransaksiRepository transactionCodeRepository;

    @Autowired
    private CabangRepository branchRepository;

    @Autowired
    private TransactionJournalRepository transactionJournalRepository;

    @Autowired
    private JournalHeaderRepository journalHeaderRepository;

    @Autowired
    private JournalDetailRepository journalDetailRepository;

    @Autowired
    private JournalLineTemplateRepository journalLineTemplateRepository;

    @Autowired
    private GlCashMovementRepository cashMovementRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductRuleRepository productRuleRepository;

    @Autowired
    private ProductRuleComponentRepository productRuleComponentRepository;

    @Autowired
    private TransactionRejectionService transactionRejectionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NomorGlAccountRepository nomorGlAccountRepository;

    @Autowired
    private TransactionCollateralRepository transactionCollateralRepository;

    //@Autowired
    //private TransactionCollateralService transactionCollateralService;

    @Override
    @Transactional
    public JournalResponse createPembayaran(JournalRequest request) {

        // =========================================================
        // 1. VALIDASI REQUEST DASAR
        // =========================================================

        List<String> errors = validateRequest(request);

        if (request == null) {
            throw new JournalValidationException(errors);
        }

        //validateCollateral(request, errors);

        // =========================================================
        // 2. CEK IDEMPOTENCY
        // =========================================================

        Optional<JournalHeader> existing =
                journalHeaderRepository.findByIdempotencyKey(
                        request.getIdempotencyKey()
                );

        boolean duplicate = existing.isPresent();

        if (duplicate) {
            errors.add("Idempotency key sudah digunakan.");
        }

        // =========================================================
        // 3. CARI PRODUCT
        // =========================================================

        Product product = productRepository
                        .findByProductCodeAndIsActiveTrue(
                                request.getProductCode()
                        )
                        .orElse(null);

        if (product == null) {

            errors.add(
                    "Product tidak ditemukan: "
                            + request.getProductCode()
            );
        }

        // Kalau product tidak ada, jangan lanjut productRule
        ProductRule productRule = null;

        if (product != null) {

            // =====================================================
            // 4. CARI PRODUCT RULE AKTIF
            // =====================================================

            LocalDate transactionDate =
                    request.getOccurredAt().toLocalDate();

            List<ProductRule> rules =
                    productRuleRepository.findActiveRules(
                            product.getId(),
                            transactionDate
                    );

            if (rules.isEmpty()) {

                errors.add(
                        "Product Rule aktif dan APPROVED tidak ditemukan "
                                + "untuk Product: "
                                + product.getProductCode()
                                + " pada tanggal "
                                + transactionDate
                );

            } else {

                productRule = rules.get(0);
            }
        }

        // =========================================================
        // 5. CARI TRANSACTION CODE
        // =========================================================

        KodeTransaksi transactionCode = transactionCodeRepository
                        .findByKode(request.getTransactionCode())
                        .orElse(null);

        if (transactionCode == null) {

            errors.add(
                    "Transaction Code tidak ditemukan: "
                            + request.getTransactionCode()
            );
        }

        // =========================================================
        // 6. CARI PRODUCT RULE COMPONENT
        // =========================================================

        List<ProductRuleComponent> components = new ArrayList<>();

        if (productRule != null) {

            components = productRuleComponentRepository
                            .findByProductRuleIdAndActiveTrue(
                                    productRule.getId()
                            );

            if (components.isEmpty()) {

                errors.add(
                        "Product Rule Component tidak ditemukan "
                                + "untuk Product Rule ID: "
                                + productRule.getId()
                );
            }
        }

        // =========================================================
        // 7. CARI BRANCH
        // =========================================================

        Cabang branch = branchRepository
                        .findByKodeCabang(
                                request.getBranchCode()
                        )
                        .orElse(null);

        if (branch == null) {

            errors.add(
                    "Branch tidak ditemukan: "
                            + request.getBranchCode()
            );
        }

        // =========================================================
        // 8. CARI MAPPING TRANSACTION CODE + BRANCH
        // =========================================================

        TransactionCodeJournal mapping = null;

        if (transactionCode != null && branch != null) {

            mapping =
                    transactionJournalRepository
                            .findByKodeTransaksiIdAndCabangId(
                                    transactionCode.getId(),
                                    branch.getId()
                            )
                            .orElse(null);

            if (mapping == null) {

                errors.add(
                        "Mapping Transaction Code dan Branch "
                                + "tidak ditemukan: "
                                + request.getTransactionCode()
                                + " / "
                                + request.getBranchCode()
                );
            }
        }

        // =========================================================
        // 9. DAPATKAN JOURNAL
        // =========================================================

        JournalId journal = null;

        if (mapping != null) {

            journal = mapping.getJournalId();

            if (journal == null) {

                errors.add(
                        "Journal ID tidak ditemukan untuk "
                                + "Transaction Code: "
                                + request.getTransactionCode()
                                + " / Branch: "
                                + request.getBranchCode()
                );
            }
        }

        // =========================================================
        // 10. CARI JOURNAL LINE TEMPLATE
        // =========================================================

        List<JournalLineTemplate> templates =
                new ArrayList<>();

        if (journal != null) {

            templates = journalLineTemplateRepository
                            .findByJournalIdOrderByLineOrderAsc(
                                    journal.getId()
                            );

            if (templates.isEmpty()) {

                errors.add(
                        "Journal Line Template tidak ditemukan "
                                + "untuk Journal ID: "
                                + journal.getId()
                );
            }
        }

        // =========================================================
        // 11. VALIDASI JOURNAL LINE
        // =========================================================

        List<PreparedLine> preparedLines = new ArrayList<>();
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalKredit = BigDecimal.ZERO;

        for (JournalLineTemplate template : templates) {

            // -----------------------------------------------------
            // 11.1 Template aktif
            // -----------------------------------------------------

            if (!Boolean.TRUE.equals(template.getIsActive())) {

                errors.add(
                        "Journal Line Template tidak aktif. ID: "
                                + template.getId()
                );

                continue;
            }

            // -----------------------------------------------------
            // 11.2 Product Rule Component
            // -----------------------------------------------------

            ProductRuleComponent component =
                    productRuleComponentRepository
                            .findById(
                                    template.getProductRuleComponentId()
                            )
                            .orElse(null);

            if (component == null) {

                errors.add(
                        "Product Rule Component belum dikonfigurasi "
                                + "pada Journal Line Template ID: "
                                + template.getId()
                );

                continue;
            }

            if (!Boolean.TRUE.equals(component.getActive())) {

                errors.add(
                        "Product Rule Component tidak aktif. ID: "
                                + component.getId()
                );

                continue;
            }

            // -----------------------------------------------------
            // 11.3 Component Master
            // -----------------------------------------------------

            if (component.getComponent() == null) {

                errors.add(
                        "Component Master belum dikonfigurasi "
                                + "pada Product Rule Component ID: "
                                + component.getId()
                );

                continue;
            }

            // -----------------------------------------------------
            // 11.4 GL ACCOUNT
            // -----------------------------------------------------

            if (template.getGlAccountId() == null) {

                errors.add(
                        "GL Account belum dikonfigurasi "
                                + "pada Journal Line Template ID: "
                                + template.getId()
                );

                continue;
            }

            var glAccount = nomorGlAccountRepository
                            .findById(
                                    template.getGlAccountId().intValue()
                            )
                            .orElse(null);

            if (glAccount == null) {

                errors.add(
                        "GL Account tidak ditemukan. ID: "
                                + template.getGlAccountId()
                                + " pada Journal Line Template ID: "
                                + template.getId()
                );

                continue;
            }

            if (!Boolean.TRUE.equals(glAccount.getIsActive())) {

                errors.add(
                        "GL Account tidak aktif: "
                                + glAccount.getNomorGl()
                                + " - "
                                + glAccount.getNamaGlAccount()
                );

                continue;
            }

            // -----------------------------------------------------
            // 11.5 VALIDASI DK
            // -----------------------------------------------------

            String dk = template.getDk();

            if (!"D".equalsIgnoreCase(dk)
                    && !"K".equalsIgnoreCase(dk)) {

                errors.add(
                        "DK tidak valid pada Journal Line Template ID: "
                                + template.getId()
                                + ". Nilai harus D atau K."
                );

                continue;
            }

            // -----------------------------------------------------
            // 11.6 RESOLVE AMOUNT
            // -----------------------------------------------------

            BigDecimal amount;

            try {

                amount =
                        resolveComponentAmount(
                                component,
                                components,
                                request
                        );

            } catch (Exception e) {

                errors.add(
                        "Gagal menentukan amount untuk Component "
                                + component.getComponent().getComponentCode()
                                + ": "
                                + e.getMessage()
                );

                continue;
            }

            if (amount == null) {

                errors.add(
                        "Amount tidak ditemukan untuk Component: "
                                + component.getComponent().getComponentCode()
                );

                continue;
            }

            if (amount.compareTo(BigDecimal.ZERO) < 0) {

                errors.add(
                        "Amount tidak boleh negatif untuk Component: "
                                + component.getComponent().getComponentCode()
                                + " = "
                                + amount
                );

                continue;
            }

            // -----------------------------------------------------
            // 11.7 SIMPAN PREPARED LINE
            // -----------------------------------------------------

            PreparedLine preparedLine =
                    new PreparedLine(
                            template,
                            component,
                            glAccount,
                            amount,
                            dk.toUpperCase()
                    );

            preparedLines.add(preparedLine);

            // -----------------------------------------------------
            // 11.8 HITUNG DEBIT / CREDIT
            // -----------------------------------------------------

            if ("D".equalsIgnoreCase(dk)) {

                totalDebit =
                        totalDebit.add(amount);

            } else {

                totalKredit =
                        totalKredit.add(amount);
            }
        }

        // =========================================================
        // 12. VALIDASI CASH MOVEMENT
        // =========================================================

        validateCashMovement(
                templates,
                request,
                errors
        );

        // =========================================================
        // 13. VALIDASI BALANCE
        // =========================================================

        // if (totalDebit.compareTo(totalKredit) != 0) {

        //     errors.add(
        //             "Journal tidak balance. "
        //                     + "Debit = "
        //                     + totalDebit
        //                     + ", Credit = "
        //                     + totalKredit
        //     );
        // }
        if (preparedLines.size() == templates.size()
                && totalDebit.compareTo(totalKredit) != 0) {

                        errors.add(
                                "Journal tidak balance. "
                                        + "Debit = "
                                        + totalDebit
                                        + ", Credit = "
                                        + totalKredit
        );
}

        // =========================================================
        // 14. EVALUASI ERROR
        // =========================================================

        if (!errors.isEmpty()) {

            // -----------------------------------------------------
            // Duplicate saja = IGNORED
            // -----------------------------------------------------

            if (duplicate && errors.size() == 1) {

                JournalHeader header = existing.get();

                String message = "Transaksi diabaikan karena "
                                + "idempotency key sudah pernah digunakan.";

                transactionRejectionService.record(

                        request.getOccurredAt().toLocalDateTime(),

                        request.getTypeTransaction(),

                        header.getCabangId(),

                        request.getBranchCode(),

                        request.getTransactionNo(),

                        request.getTransactionCode(),

                        message,

                        toJson(request),

                        TransactionRejectionStatus.IGNORED
                );

                return JournalResponse.ignored(
                        message,
                        header.getJournalNo()
                );
            }

            // -----------------------------------------------------
            // Ada error lain = REJECTED
            // -----------------------------------------------------

            throw new JournalValidationException(errors);
        }

        // =========================================================
        // 15. POSTING JOURNAL HEADER
        // =========================================================

        JournalHeader header = new JournalHeader();
        header.setJournalId(
                journal.getId()
        );

        header.setTransactionCodeId(
                transactionCode.getId().longValue()
        );

        header.setCabangId(
                branch.getId().longValue()
        );

        header.setTransactionNo(
                request.getTransactionNo()
        );

        header.setTransactionDate(
                request.getOccurredAt()
                        .toLocalDateTime()
        );

        header.setStatus("POSTED");

        header.setCreatedAt(
                LocalDateTime.now()
        );

        header.setIdempotencyKey(request.getIdempotencyKey());

        JournalHeader savedHeader = journalHeaderRepository.save(header);

        // =========================================================
        // 16. GENERATE JOURNAL NUMBER
        // =========================================================

        Long headerId = savedHeader.getId();

        String journalNo =
                String.format(
                        "%s/JV/%tY/%<tm/%06d",
                        request.getBranchCode(),
                        LocalDateTime.now(),
                        headerId
                );

        savedHeader.setJournalNo(journalNo);

        journalHeaderRepository.save(savedHeader);

        // =========================================================
        // 17. POSTING JOURNAL DETAIL
        // =========================================================

        for (PreparedLine line : preparedLines) {

            JournalDetail detail =
                    new JournalDetail();

            detail.setJournalHeaderId(
                    savedHeader.getId()
            );

            detail.setGlAccountId(
                    line.glAccount()
                            .getId()
                            .longValue()
            );

            detail.setDk(
                    line.dk()
            );

            detail.setAmount(
                    line.amount()
            );

            detail.setDescription(
                    line.template()
                            .getDescription()
            );

            detail.setCreatedAt(
                    LocalDateTime.now()
            );

            journalDetailRepository.save(detail);
        }

        // =========================================================
        // 18. POSTING CASH MOVEMENT
        // =========================================================

        createCashMovements(
                preparedLines,
                savedHeader,
                request
        );
        // 18.1 SIMPAN COLLATERAL PENCAIRAN
        // if ("TX-DISBURSE".equalsIgnoreCase(request.getTransactionCode())) {
        // saveCollateral(request);
        // }

        // =========================================================
        // 19. SELESAI
        // =========================================================

        return JournalResponse.posted(journalNo);
    }

    private record PreparedLine(
            JournalLineTemplate template,
            ProductRuleComponent component,
            NomorGlAccount glAccount,
            BigDecimal amount,
            String dk
    ) {
    }

    // =============================================================
    // CASH MOVEMENT
    // =============================================================

    private void createCashMovements(
            List<PreparedLine> preparedLines,
            JournalHeader savedHeader,
            JournalRequest request) {

        for (PreparedLine line : preparedLines) {

            JournalLineTemplate template = line.template();

            String movementType = template.getCashMovementType();

            // NONE = bukan pergerakan kas
            if ("NONE".equalsIgnoreCase(movementType)) {
                continue;
            }

            BigDecimal movementAmount = line.amount();

            // Nominal 0 tidak perlu dibuat
            if (movementAmount.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            GlCashMovement cashMovement = new GlCashMovement();

            cashMovement.setJournalHeaderId(
                    savedHeader.getId()
            );

            cashMovement.setDirection(
                    movementType.toUpperCase()
            );

            cashMovement.setAmount(
                    movementAmount
            );

            // =====================================================
            // ACTOR
            // =====================================================

            cashMovement.setTellerUserId(
                    request.getActor()
                            .getTellerUserId()
            );

            cashMovement.setKasPointCode(
                    request.getActor()
                            .getKasPointCode()
            );

            // =====================================================
            // COUNTERPARTY
            // =====================================================

            if (request.getCounterparty() != null) {

                cashMovement.setCounterpartyType(
                        request.getCounterparty()
                                .getType()
                );

                cashMovement.setCounterpartyRef(
                        request.getCounterparty()
                                .getRef()
                );

                cashMovement.setCounterpartyName(
                        request.getCounterparty()
                                .getName()
                );
            }

            // =====================================================
            // WAKTU
            // =====================================================

            cashMovement.setOccurredAt(
                    request.getOccurredAt()
                            .toLocalDateTime()
            );

            // =====================================================
            // DESCRIPTION
            // =====================================================

            String componentName =
                    line.component()
                            .getComponent()
                            .getComponentName();

            cashMovement.setDescription(
                    componentName
                            + " - "
                            + request.getTransactionNo()
            );

            cashMovementRepository.save(
                    cashMovement
            );
        }
    }

    // =============================================================
    // VALIDASI REQUEST
    // =============================================================

    private List<String> validateRequest(JournalRequest request) {

        List<String> errors =  new ArrayList<>();

        if (request == null) {

            errors.add(
                    "Request tidak boleh null"
            );

            return errors;
        }

        if (request.getTypeTransaction() == null || request.getTypeTransaction().isBlank()) {

            errors.add(
                    "typeTransaction wajib diisi"
            );
        }

        if (!"pembayaran".equalsIgnoreCase(request.getTypeTransaction())) {
            errors.add(
                    "typeTransaction type harus pembayaran karena anda menggunaka API pembayaran"
            );
        }
       

        if (request.getTransactionCode() == null
                || request.getTransactionCode().isBlank()) {

            errors.add(
                    "transaction_code wajib diisi"
            );
        }


        if (request.getTransactionCode() == null
                || request.getTransactionCode().isBlank()) {

            errors.add(
                    "transaction_code wajib diisi"
            );
        }

        if (request.getBranchCode() == null
                || request.getBranchCode().isBlank()) {

            errors.add(
                    "branch_code wajib diisi"
            );
        }

        if (request.getIdempotencyKey() == null
                || request.getIdempotencyKey().isBlank()) {

            errors.add(
                    "idempotency_key wajib diisi"
            );
        }

        if (request.getProductCode() == null
                || request.getProductCode().isBlank()) {

            errors.add(
                    "product_code wajib diisi"
            );
        }

        if (request.getOccurredAt() == null) {

            errors.add(
                    "occurred_at wajib diisi"
            );
        }

        if (request.getTransactionNo() == null
                || request.getTransactionNo().isBlank()) {

            errors.add(
                    "transaction_no wajib diisi"
            );
        }

        return errors;
    }

    // =============================================================
    // RESOLVE AMOUNT
    // =============================================================

    private BigDecimal resolveAmount(String amountSource, JournalRequest request) {

        if (amountSource == null || amountSource.isBlank()) {

            throw new IllegalArgumentException("Amount source tidak boleh kosong");
        }

        if (request.getAmounts() == null) {

            throw new IllegalArgumentException("Amounts wajib diisi");
        }

        switch (amountSource) {

            // PENCAIRAN

            case "loan_amount":
                return request.getAmounts()
                        .getLoanAmount();

            case "repayment_amount":
                return request.getAmounts()
                        .getRepaymentAmount();

            case "admin_cost":
                return request.getAmounts()
                        .getAdminCost();

            case "provision":
                return request.getAmounts()
                        .getProvision();

            case "customer_cash":
                return request.getAmounts().getCustomerCash();

            // =====================================================
            // PEMBAYARAN
            // =====================================================

            case "payment_amount":
                return request.getAmounts()
                        .getPaymentAmount();

            case "principal_payment":
                return request.getAmounts()
                        .getPrincipalPayment();

            case "interest_amount":
                return request.getAmounts()
                        .getInterestAmount();

            // TIDAK DIKENALI  

            default:

                throw new IllegalArgumentException(
                        "Amount source tidak dikenali: "
                                + amountSource
                );
        }
    }

    // =========================================================
    // RESOLVE COMPONENT AMOUNT
    // =========================================================

    private BigDecimal resolveComponentAmount(
            ProductRuleComponent component,
            List<ProductRuleComponent> components,
            JournalRequest request) {

        String calculationType =
                component.getCalculationType();

        if (calculationType == null
                || calculationType.isBlank()) {

            throw new IllegalStateException(
                    "Calculation Type belum dikonfigurasi untuk Component: "
                            + component.getComponent()
                            .getComponentCode()
            );
        }

        switch (calculationType) {

            case "INPUT":
                return resolveInputAmount(
                        component,
                        request
                );

            case "FIXED":
                return resolveFixedAmount(
                        component
                );

            case "CALCULATED":
                return resolveCalculatedAmount(
                        component,
                        components,
                        request
                );

            default:
                throw new IllegalStateException(
                        "Calculation Type tidak dikenali: "
                                + calculationType
                                + " untuk Component: "
                                + component.getComponent()
                                .getComponentCode()
                );
        }
    }

    private BigDecimal resolveInputAmount(ProductRuleComponent component, JournalRequest request) {
        String amountSource = component.getAmountSource();
        if (amountSource == null || amountSource.isBlank()) {
            throw new IllegalStateException("Amount Source wajib diisi untuk Calculation Type INPUT. Component: " + component.getComponent().getComponentCode());
        }
        return resolveAmount(amountSource, request);
    }

    private BigDecimal resolveFixedAmount(ProductRuleComponent component) {
        BigDecimal fixedAmount = component.getfixedAmount();
        if (fixedAmount == null) {
            throw new IllegalStateException("Fixed Amount wajib diisi untuk Component: " + component.getComponent().getComponentCode());
        }
        return fixedAmount;
    }

    private BigDecimal resolveCalculatedAmount(
            ProductRuleComponent component,
            List<ProductRuleComponent> components,
            JournalRequest request) {

        throw new UnsupportedOperationException(
                "Calculation Type CALCULATED belum diimplementasikan."
        );
    }

    private String toJson(JournalRequest request) {

        try {
            return objectMapper.writeValueAsString(request);

        } catch (JsonProcessingException e) {

            return "{}";
        }
    }

    private void validateCashMovement(
            List<JournalLineTemplate> templates,
            JournalRequest request,
            List<String> errors) {

        boolean requiresCashMovement = false;
        for (JournalLineTemplate template : templates) {

            String movementType = template.getCashMovementType();

            if (movementType == null || movementType.isBlank()) {

                errors.add(
                        "Cash Movement Type belum dikonfigurasi "
                                + "pada Journal Line Template ID: "
                                + template.getId()
                );

                continue;
            }

            if (!"IN".equalsIgnoreCase(movementType)
                    && !"OUT".equalsIgnoreCase(movementType)
                    && !"NONE".equalsIgnoreCase(movementType)) {

                errors.add(
                        "Cash Movement Type tidak valid pada "
                                + "Journal Line Template ID: "
                                + template.getId()
                                + ". Nilai harus IN, OUT, atau NONE."
                );

                continue;
            }

            if ("NONE".equalsIgnoreCase(movementType)) {
                continue;
            }

            requiresCashMovement = true;
        }

        // =========================================================
        // ACTOR
        // =========================================================

        if (requiresCashMovement) {

            if (request.getActor() == null) {

                errors.add(
                        "Actor wajib diisi karena transaksi "
                                + "menghasilkan Cash Movement."
                );

            } else {

                if (request.getActor().getTellerUserId() == null) {

                    errors.add(
                            "teller_user_id wajib diisi karena transaksi "
                                    + "menghasilkan Cash Movement."
                    );
                }

                if (request.getActor().getKasPointCode() == null
                        || request.getActor()
                        .getKasPointCode()
                        .isBlank()) {

                    errors.add(
                            "kas_point_code wajib diisi karena transaksi "
                                    + "menghasilkan Cash Movement."
                    );
                }
            }

            // =====================================================
            // COUNTERPARTY
            // =====================================================

            if (request.getCounterparty() == null) {

                errors.add("Counterparty wajib diisi karena transaksi "
                                + "menghasilkan Cash Movement."
                );

            } else {

                if (request.getCounterparty().getType() == null
                        || request.getCounterparty()
                        .getType()
                        .isBlank()) {

                    errors.add(
                            "counterparty.type wajib diisi."
                    );
                }

                if (request.getCounterparty().getRef() == null
                        || request.getCounterparty()
                        .getRef()
                        .isBlank()) {

                    errors.add(
                            "counterparty.ref wajib diisi."
                    );
                }
            }


           


        }
    }

        private void validateCollateral(JournalRequest request,List<String> errors) {
                // Hanya validasi collateral untuk pencairan
                // if (!"TX-DISBURSE".equalsIgnoreCase(request.getTransactionCode())) {
                //         return;
                // }

                JournalRequest.Collateral collateral = request.getCollateral();
                if (collateral == null) {
                        errors.add("collateral wajib diisi untuk transaksi pencairan.");
                        return;
                }
                if (collateral.getCollateralType() == null || collateral.getCollateralType().isBlank()) {
                        errors.add("collateral_type wajib diisi.");
                }

                if (collateral.getAppraisedValue() == null || collateral.getAppraisedValue().compareTo(BigDecimal.ZERO) <= 0) {
                        errors.add("appraised_value harus lebih besar dari 0.");
                }

                if (collateral.getLtv() == null || collateral.getLtv().compareTo(BigDecimal.ZERO) < 0 || collateral.getLtv().compareTo(new BigDecimal("100")) > 0) {
                        errors.add("ltv harus berada antara 0 sampai 100.");
                }
        }


        // private void saveCollateral(JournalRequest request) {

        //         JournalRequest.Collateral collateral = request.getCollateral();

        //         if (collateral == null) {
        //                 return;
        //         }

        //         TransactionCollateral entity = new TransactionCollateral();

        //         entity.setTransactionNo(
        //                 request.getTransactionNo()
        //         );

        //         entity.setCollateralType(
        //                 collateral.getCollateralType()
        //         );

        //         entity.setAppraisedValue(
        //                 collateral.getAppraisedValue()
        //         );

        //         entity.setLtv(
        //                 collateral.getLtv()
        //         );

        //         transactionCollateralRepository.save(entity);
        // }

}