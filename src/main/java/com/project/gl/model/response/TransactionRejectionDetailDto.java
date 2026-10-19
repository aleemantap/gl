package com.project.gl.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransactionRejectionDetailDto {

    private Long id;

    private String transactionCode;

    private String payload;

}