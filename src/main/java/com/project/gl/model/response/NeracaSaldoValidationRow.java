package com.project.gl.model.response;


import java.time.LocalDateTime;

import com.project.gl.model.enums.NeracaSaldoStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NeracaSaldoValidationRow {

    private Long id;
    private Long submissionId;

    private String kodeCabang;
    private String namaCabang;

    private NeracaSaldoStatus status;

    private String validatedBy;
    private LocalDateTime validatedAt;
    private String note;
}