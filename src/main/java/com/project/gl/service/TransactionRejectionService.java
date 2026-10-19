package com.project.gl.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import com.project.gl.model.entity.TransactionRejection;
import com.project.gl.model.enums.TransactionRejectionStatus;
import com.project.gl.model.response.TransactionRejectionDetailDto;
import com.project.gl.model.response.TransactionRejectionDto;
import com.project.gl.repo.TransactionRejectionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.project.gl.helper.Util;
// import java.time.format.DateTimeFormatter;



@Service
@RequiredArgsConstructor
public class TransactionRejectionService {

    private final TransactionRejectionRepository repository;
    private final Util util;
	   

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(
            LocalDateTime transactionTime,
            String typeTransaction,
            Long cabangId,
            String branchCode,
            String transactionNo,
            String transactionCode,
            String reason,
            String payload,
            TransactionRejectionStatus status
           
        ) {

        TransactionRejection rejection = new TransactionRejection();

        rejection.setTransactionTime(transactionTime);

        rejection.setTypeTransaction(typeTransaction);

        rejection.setCabangId(cabangId);

        rejection.setBranchCode(branchCode);

        rejection.setTransactionNo(transactionNo);
        // System.out.println("TESSSSSS=="+transactionCode);
        rejection.setTransactionCode(transactionCode);

        rejection.setRejectionReason(reason);

        rejection.setPayload(payload);

        rejection.setStatus(status);

      

        repository.save(rejection);
    }

    public List<TransactionRejectionDto> findAll() {

        return repository.findAll()
                .stream()
                .map(item ->
                    new TransactionRejectionDto(
                        item.getId(),
                        item.getTransactionTime(),
                        item.getTypeTransaction(),
                        item.getCabangId(),
                        null,
                        item.getTransactionCode(),
                        item.getRejectionReason(),
                        item.getStatus().name()
                    )
                )
                .toList();
    }

    public TransactionRejectionDetailDto findDetail(Long id) {

        TransactionRejection item =
                repository.findById(id)
                    .orElseThrow(() ->
                        new IllegalArgumentException(
                            "Data transaksi ditolak tidak ditemukan"
                        )
                    );

        return new TransactionRejectionDetailDto(
            item.getId(),
            item.getTransactionCode(),
            item.getPayload()
        );
    }


      public Page<TransactionRejection> getTransactionRejections(
            String status,
            String fromDate,
            String toDate,
            int page,
            int size,
            String sortColumn,
            String sortDirection) {


        // Convert status string to enum
        TransactionRejectionStatus statusEnum = null;
        if (status != null && !status.isEmpty()) {
            try {
                statusEnum = TransactionRejectionStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                // Invalid status, keep null
            }
        }

        // Parse dates
        LocalDateTime fromDateTime = null;
        LocalDateTime toDateTime = null;
        
        if (fromDate != null && !fromDate.isEmpty()) {
            fromDateTime = LocalDateTime.parse(fromDate + "T00:00:00");
        }
        
        if (toDate != null && !toDate.isEmpty()) {
            toDateTime = LocalDateTime.parse(toDate + "T23:59:59");
        }

        // Create sort
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Sort sort = Sort.by(direction, sortColumn);
        Pageable pageable = PageRequest.of(page, size, sort);

        return repository.findByFilters(statusEnum, fromDateTime, toDateTime, pageable);
    }

     public List<TransactionRejection> findAllWithFiltersExport(
            String idsJson,
            String status,
            String fromDate,
            String toDate
           ) {

        // Convert status string to enum
        TransactionRejectionStatus statusEnum = null;
        if (status != null && !status.isEmpty()) {
            try {
                statusEnum = TransactionRejectionStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                // Invalid status, keep null
            }
        }

        // Parse dates
        LocalDateTime fromDateTime = null;
        LocalDateTime toDateTime = null;
        
        if (fromDate != null && !fromDate.isEmpty()) {
            fromDateTime = LocalDateTime.parse(fromDate + "T00:00:00");
        }
        
        if (toDate != null && !toDate.isEmpty()) {
            toDateTime = LocalDateTime.parse(toDate + "T23:59:59");
        }
        List<Long> ids = util.parseIdsJson(idsJson);

        boolean idsEmpty = (ids == null || ids.isEmpty());
        List<Long> safeIds = idsEmpty ? List.of(-1L) : ids;        
     
        return repository.findByFiltersExpor(safeIds,idsEmpty, statusEnum, fromDateTime, toDateTime);
    }

    
}