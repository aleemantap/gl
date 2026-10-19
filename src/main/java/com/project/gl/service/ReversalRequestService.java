package com.project.gl.service;

import com.project.gl.model.entity.ReversalRequest;
import com.project.gl.model.enums.ReversalRequestStatus;
import com.project.gl.model.response.ReversalRequestDetail;
import com.project.gl.model.response.ReversalRequestRow;

import java.util.List;

public interface ReversalRequestService {
    ReversalRequestDetail findDetail(Long id);
    List<ReversalRequest> findAll();

    List<ReversalRequest> findByStatus(
            ReversalRequestStatus status
    );

    ReversalRequest findById(Long id);

    List<ReversalRequestRow> findAllRows();

    ReversalRequest createRequest(
            Long originalJournalId,
            String requestedBy,
            String reason,
            String description
    );

    void approve(
            Long id,
            String approvedBy
    );

    void reject(
            Long id,
            String rejectedBy,
            String rejectionReason
    );

    //List<ReversalRequest> findAll();
}