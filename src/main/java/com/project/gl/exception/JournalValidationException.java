package com.project.gl.exception;

import java.util.List;

import lombok.Getter;

@Getter
public class JournalValidationException extends RuntimeException {

    private final List<String> errors;

    public JournalValidationException(List<String> errors) {
        super("Transaksi ditolak karena terdapat kesalahan.");
        this.errors = errors;
    }
}