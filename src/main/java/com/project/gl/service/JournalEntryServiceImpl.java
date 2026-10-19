package com.project.gl.service;

import com.project.gl.model.response.JournalEntryResponse;
import com.project.gl.repo.JournalEntryRepository;
import com.project.gl.service.JournalEntryService;
// import com.project.util.PageResponse;
import com.project.gl.model.response.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
@Slf4j
public class JournalEntryServiceImpl implements JournalEntryService {

    @Autowired
    private  JournalEntryRepository journalEntryRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<JournalEntryResponse> getJournalEntriesWithFilters(
            int page,
            int size,
            String sortBy,
            String orderDir,
            LocalDateTime periodeStart,
            LocalDateTime  periodeEnd,
            Integer branchId,
            String kodeTc
            ) {

        // Validate and clean search value
        // if (searchValue != null && searchValue.isEmpty()) {
        //     searchValue = null;
        // }

    

        // Create sort
        Sort.Direction direction = Sort.Direction.fromString(orderDir);
        Sort sort = Sort.by(direction, getSortField(sortBy));
        
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<JournalEntryResponse> pageResult = journalEntryRepository
                .findJournalEntriesWithFilters(
                        periodeStart,
                        periodeEnd,
                        branchId,
                        kodeTc,                      
                        pageable
                );

        // Convert to PageResponse
        return new PageResponse<>(
                pageResult.getContent(),
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.isLast()
        );
    }

    private String getSortField(String sortBy) {
        // Map sortBy dari frontend ke entity field
        switch (sortBy) {
            case "nomorGl":
                return "ga.nomorGl";
            case "namaGlAccount":
                return "ga.namaGlAccount";
            case "branchCode":
                return "b.kodeCabang";
            case "transactionCode":
                return "tc.kode";
            case "transactionDate":
                return "jh.transactionDate";
            case "journalNo":
                return "jh.journalNo";
            case "amount":
                return "jd.amount";
            case "status":
                return "jh.status";
            case "dk":
                return "jd.dk";
            default:
                return "jh.id";
        }
    }
}