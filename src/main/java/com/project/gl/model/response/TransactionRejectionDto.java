package com.project.gl.model.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransactionRejectionDto {

    private Long id;

    private LocalDateTime transactionTime;

    private String typeTransaction;

    private Long cabangId;

    private String cabangName;

    private String transactionCode;

    private String rejectionReason;

    private String status;

}