package com.project.gl.service;

import java.time.YearMonth;

import com.project.gl.model.response.GlCkpnPreviewResponse;

public interface GlCkpnPostingService {

    // String posting(GlCkpnPreviewResponse preview);

     String posting(YearMonth periode, Integer cabangId, String kodeCabang);
}