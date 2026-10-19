package com.project.gl.service;

import com.project.gl.model.response.JournalDetailLineResponse;
import com.project.gl.model.response.JournalDetailResponse;
import com.project.gl.model.response.JournalListDTO;
import com.project.gl.model.response.PageResponse;
import com.project.gl.repo.JournalHeaderRepository;
import com.project.gl.repo.JournalDetailRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.gl.exception.ResourceNotFoundException;
import com.project.gl.model.entity.JournalDetail;
import com.project.gl.model.entity.JournalHeader;
import com.project.gl.model.projection.JournalListProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
public class JournalReportServiceImpl implements JournalReportService {

    private final JournalHeaderRepository journalHeaderRepository;
    private final JournalDetailRepository journalDetailRepository;
    private final ObjectMapper objectMapper;

   
    @Override
    public PageResponse<JournalListDTO> findJournalList(
            int page,
            int size,
            String sortBy,
            String orderDir,
            LocalDate fromDate,
            LocalDate toDate,
            String branchCode,
            String transactionCode,
            String journalNo,
            String transactionNo,
            String status,
            String statusReversal
    ) {

        LocalDateTime fromDateTime = null;
        LocalDateTime toDateTime = null;

        if (fromDate != null) {
            fromDateTime = fromDate.atStartOfDay();
        }

        if (toDate != null) {
            toDateTime = toDate
                    .plusDays(1)
                    .atStartOfDay();
        }

        Sort sort = Sort.by(
                "transactionDate"
        );

        if ("asc".equalsIgnoreCase(orderDir)) {
            sort = Sort.by(Sort.Direction.ASC, "transactionDate");
        } else {
            sort = Sort.by(Sort.Direction.DESC, "transactionDate");
        }

        Pageable pageable =
                PageRequest.of(page, size, sort);

        Page<JournalListProjection> result =
                journalHeaderRepository.findJournalList(
                        fromDateTime,
                        toDateTime,
                        branchCode,
                        transactionCode,
                        journalNo,
                        transactionNo,
                        status,
                        statusReversal,
                        pageable
                );

        List<JournalListDTO> content =
                result.getContent()
                        .stream()
                        .map(row ->
                                new JournalListDTO(
                                        row.getId(),
                                        row.getJournalNo(),
                                        row.getTransactionNo(),
                                        row.getTransactionCode(),
                                        row.getBranchCode(),
                                        row.getTransactionDate(),
                                        row.getTotalDebit(),
                                        row.getTotalCredit(),
                                        row.getStatus(),
                                        row.getReversalStatus(),
                                        row.getNamaCabang()
                                )
                        )
                        .toList();

        return new PageResponse<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isLast()
        );
    }
    @Override
    public JournalDetailResponse getJournalDetail(String journalNo) {

        JournalHeader header = journalHeaderRepository
                        .findByJournalNo(journalNo)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Journal tidak ditemukan: " + journalNo
                                )
                        );

        List<JournalDetail> details = journalDetailRepository.findByJournalHeaderIdOrderByIdAsc(
                                header.getId()
                        );

        if (details.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Journal Detail tidak ditemukan untuk Journal: "
                            + journalNo
            );
        }

        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        List<JournalDetailLineResponse> lines = new ArrayList<>();

        for (JournalDetail detail : details) {

            if ("D".equals(detail.getDk())) {
                totalDebit = totalDebit.add(detail.getAmount());

            } else if ("K".equals(detail.getDk())) {
                totalCredit = totalCredit.add(detail.getAmount());
            }

            JournalDetailLineResponse line = new JournalDetailLineResponse();

            line.setId(detail.getId());
            line.setGlAccountId(detail.getGlAccountId());
            line.setDk(detail.getDk());
            line.setAmount(detail.getAmount());
            line.setDescription(detail.getDescription());

            if (detail.getNomorGlAccount() != null) {

                line.setGlAccountCode(detail.getNomorGlAccount().getNomorGl());
                line.setGlAccountName(detail.getNomorGlAccount().getNamaGlAccount());
            }

            lines.add(line);
        }

        return new JournalDetailResponse(
                header.getId(),
                header.getJournalNo(),
                header.getTransactionNo(),
                null,
                null,
                header.getTransactionDate(),
                header.getStatus(),
                totalDebit,
                totalCredit,
                lines
        );
    }

    @Override
    public List<JournalListDTO> findJournalListForExport(
            String idsJson,
            String fromDate,
            String toDate,
            String branchCode,
            String transactionCode,
            String journalNo,
            String transactionNo,
            String status,
            String statusReversal) {

        // 1. Parsing JSON 'ids' -> List<Long>
        List<Long> listIds = parseIdsJson(idsJson);

        // 2. Parsing Tanggal
        LocalDate from = (fromDate != null && !fromDate.isBlank()) ? LocalDate.parse(fromDate) : null;
        LocalDate to = (toDate != null && !toDate.isBlank()) ? LocalDate.parse(toDate) : null;

        // Ubah ke LocalDateTime jika query repository kamu butuh LocalDateTime
        LocalDateTime fromDateTime = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime toDateTime = (to != null) ? to.plusDays(1).atStartOfDay() : null;

        // 3. Panggil Repository khusus export (mengembalikan List<JournalListProjection>)
        List<JournalListProjection> projections = journalHeaderRepository.findJournalListForExport(
                listIds,
                fromDateTime,
                toDateTime,
                cleanString(branchCode),
                cleanString(transactionCode),
                cleanString(journalNo),
                cleanString(transactionNo),
                cleanString(status),
                cleanString(statusReversal)
        );

     // 4. Map Projection ke DTO
     return projections.stream()
        .map(p -> JournalListDTO.builder()
                .id(p.getId())
                .journalNo(p.getJournalNo())
                .transactionNo(p.getTransactionNo())
                .transactionCode(p.getTransactionCode())
                .branchCode(p.getBranchCode())
                .namaCabang(p.getNamaCabang())
                .transactionDate(p.getTransactionDate())
                .totalDebit(p.getTotalDebit())
                .totalCredit(p.getTotalCredit())
                .status(p.getStatus())
                .reversalStatus(p.getReversalStatus())
                .build())
        .collect(Collectors.toList());
    }

    private List<Long> parseIdsJson(String idsJson) {
        if (idsJson == null || idsJson.trim().isEmpty()) {
            return null;
        }
        try {
            List<Long> ids = objectMapper.readValue(idsJson, new TypeReference<List<Long>>() {});
            return (ids != null && !ids.isEmpty()) ? ids : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String cleanString(String input) {
        return (input != null && !input.isBlank()) ? input.trim() : null;
    }

    
}