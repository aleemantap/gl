package com.project.gl.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.gl.exception.JournalValidationException;
import com.project.gl.model.enums.TransactionRejectionStatus;
import com.project.gl.model.request.JournalRequest;
import com.project.gl.model.response.JournalResponse;
import com.project.gl.service.JournalService;
import com.project.gl.service.api.PencairanService;

import com.project.gl.service.TransactionRejectionService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/v1")
public class ApiPencairanController {

    private final PencairanService pencairanService;
    private final TransactionRejectionService transactionRejectionService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ApiPencairanController(
            PencairanService pencairanService,
            TransactionRejectionService transactionRejectionService,
            ObjectMapper objectMapper) {

        this.pencairanService = pencairanService;
        this.transactionRejectionService = transactionRejectionService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/pencairan")
    public ResponseEntity<?> createJournal(@RequestBody String rawPayload) {
        JournalRequest request = null;
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String clientId = authentication.getName();
            System.out.println("CLIENT-ID"+clientId);
            //--- nanti clinetID bila diperlukan masuk ke dalam DB sekarang bleum ada requirmentnyya---


            // =====================================================
            // 1. JSON ASLI DARI API
            // =====================================================
            request = objectMapper.readValue(
                    rawPayload,
                    JournalRequest.class
            );

            // =====================================================
            // 2. PROSES JOURNAL
            // =====================================================
            JournalResponse response = pencairanService.createPencairan(request);

            return ResponseEntity.ok(response);

        } catch (JournalValidationException e) {

            String reason =
                    String.join("; ", e.getErrors());

            transactionRejectionService.record(

                    request != null
                            && request.getOccurredAt() != null
                            ? request.getOccurredAt()
                                    .toLocalDateTime()
                            : LocalDateTime.now(),
                    
                    request != null
                            ? request.getTypeTransaction()
                            : null,

                    null,

                    request != null
                            ? request.getBranchCode()
                            : null,

                    request != null
                            ? request.getTransactionNo()
                            : null,

                    request != null
                            ? request.getTransactionCode()
                            : null,

                    reason,

                    rawPayload,

                    TransactionRejectionStatus.REJECTED
            );

            return ResponseEntity
                    .unprocessableEntity()
                    .body(
                            JournalResponse.rejected(
                                    e.getErrors()
                            )
                    );

        } catch (Exception e) {

            transactionRejectionService.record(

                    request != null
                            && request.getOccurredAt() != null
                            ? request.getOccurredAt()
                                    .toLocalDateTime()
                            : LocalDateTime.now(),
                            
                    request != null
                            ? request.getTypeTransaction()
                            : null,

                    null,

                    request != null
                            ? request.getBranchCode()
                            : null,

                    request != null
                            ? request.getTransactionNo()
                            : null,

                    request != null
                            ? request.getTransactionCode()
                            : null,

                    e.getMessage(),

                    rawPayload,

                    TransactionRejectionStatus.REJECTED
            );

            return ResponseEntity
                    .unprocessableEntity()
                    .body(
                            JournalResponse.rejected(
                                    List.of(e.getMessage())
                            )
                    );
        }
    }

}

