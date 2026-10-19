package com.project.gl.model.response;

import java.util.List;

public class JournalResponse {

    private String status;
    private String message;
    private String journalNo;
    private List<String> errors;

    // No-args constructor
    public JournalResponse() {
    }

    // Convenience constructor
    public JournalResponse(String status, String journalNo) {
        this.status = status;
        this.journalNo = journalNo;
    }

    // All-args constructor (Dibutuhkan oleh static factory methods)
    public JournalResponse(String status, String message, String journalNo, List<String> errors) {
        this.status = status;
        this.message = message;
        this.journalNo = journalNo;
        this.errors = errors;
    }

    // Static Factory Method: POSTED
    public static JournalResponse posted(String journalNo) {
        return new JournalResponse(
                "POSTED",
                null,
                journalNo,
                null
        );
    }

    // Static Factory Method: IGNORED
    public static JournalResponse ignored(String message, String journalNo) {
        return new JournalResponse(
                "IGNORED",
                message,
                journalNo,
                null
        );
    }

    // Static Factory Method: REJECTED
    public static JournalResponse rejected(List<String> errors) {
        return new JournalResponse(
                "REJECTED",
                "Transaksi ditolak karena terdapat kesalahan.",
                null,
                errors
        );
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getJournalNo() {
        return journalNo;
    }

    public void setJournalNo(String journalNo) {
        this.journalNo = journalNo;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}