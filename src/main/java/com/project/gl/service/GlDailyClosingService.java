package com.project.gl.service;
import java.time.LocalDate;
import java.util.Map;

public interface GlDailyClosingService {

    Map<String, Object> findStatus(
            LocalDate tanggal,
            Integer cabangId
    );

    Map<String, Object> check(
            LocalDate tanggal,
            Integer cabangId
    );

    Map<String, Object> close(
            LocalDate tanggal,
            Integer cabangId,
            String username
    );

    Map<String, Object> reopen(
            LocalDate tanggal,
            Integer cabangId,
            String username,
            String reason
    );
}
