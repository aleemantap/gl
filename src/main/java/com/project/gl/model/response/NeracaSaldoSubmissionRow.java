package com.project.gl.model.response;


import java.time.LocalDate;
import java.time.LocalDateTime;
import com.project.gl.model.enums.NeracaSaldoStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NeracaSaldoSubmissionRow {

    private Long id;
    private LocalDate periode;
    private Long unitId;

    private String kodeCabang;
    private String namaCabang;

    private NeracaSaldoStatus status;

    private String submittedBy;
    private LocalDateTime submittedAt;
}