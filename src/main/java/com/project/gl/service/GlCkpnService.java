package com.project.gl.service;

import java.time.YearMonth;

import com.project.gl.model.response.GlCkpnPreviewResponse;

public interface GlCkpnService {

    GlCkpnPreviewResponse preview(
            YearMonth periode,
            Integer cabangId,
            String kodeCabang
    );
}