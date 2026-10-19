package com.project.gl.service;

import com.project.gl.model.response.JournalEntryResponse;
import com.project.gl.model.response.PageResponse;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;

public interface JournalEntryService {
    
    PageResponse<JournalEntryResponse> getJournalEntriesWithFilters(
            int page,
            int size,
            String sortBy,
            String orderDir,
            LocalDateTime periodeStart,
            LocalDateTime  periodeEnd,
            Integer cabangId,
            String kodeTc
    );
}
