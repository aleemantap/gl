package com.project.gl.service.api;

import com.project.gl.model.request.JournalRequest;
import com.project.gl.model.response.JournalResponse;

public interface PembayaranService {

    JournalResponse createPembayaran(JournalRequest request);
}