package com.project.gl.service;
import com.project.gl.model.request.JournalRequest;
import com.project.gl.model.response.JournalResponse;

public interface JournalService {

    JournalResponse createJournal(JournalRequest request);
}