package com.project.gl.service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

import com.project.gl.model.entity.GlDailyClosing;
import com.project.gl.repo.GlDailyClosingRepository;
import com.project.gl.service.GlDailyClosingService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GlDailyClosingServiceImpl implements GlDailyClosingService {

    private final GlDailyClosingRepository repository;

    @Override
    public Map<String, Object> findStatus(LocalDate tanggal, Integer cabangId) {

        Map<String, Object> result = new HashMap<>();
        String status = repository
                .findByTanggalAndCabangId(tanggal, cabangId)
                .map(GlDailyClosing::getStatus)
                .orElse("TERBUKA");

        result.put("tanggal", tanggal);
        result.put("cabangId", cabangId);
        result.put("statusClosing", status);

        return result;
    }

    
    @Override
    public Map<String, Object> check(LocalDate tanggal, Integer cabangId) {

        Map<String, Object> result = new HashMap<>();
        // Data pemeriksaan sementara
        result.put("totalAntrean", 0);
        result.put("statusAntrean", "OK");

        result.put("totalRejected", 0);
        result.put("statusRejected", "OK");

        result.put("kasGl", 12500000);
        result.put("kasGadai", 12500000);
        result.put(
                "hasilPerbandinganKas",
                "12.500.000 = 12.500.000"
        );
        result.put("statusKas", "OK");

        // Status closing dari database
        Map<String, Object> statusData =
                findStatus(tanggal, cabangId);

        result.put(
                "statusClosing",
                statusData.get("statusClosing")
        );

        result.put("success", true);

        return result;
    }


        
    @Override
    @Transactional
    public Map<String, Object> close(
            LocalDate tanggal,
            Integer cabangId,
            String username) {

        Map<String, Object> result = new HashMap<>();

        // Validasi parameter
        if (tanggal == null || cabangId == null) {
            result.put("success", false);
            result.put("message", "Tanggal dan cabang wajib diisi.");
            return result;
        }

        // Cari record berdasarkan tanggal dan cabang
        GlDailyClosing closing = repository
                .findByTanggalAndCabangId(tanggal, cabangId)
                .orElseGet(() -> {
                    GlDailyClosing newClosing = new GlDailyClosing();
                    newClosing.setTanggal(tanggal);
                    newClosing.setCabangId(cabangId);
                    return newClosing;
                });

        // Cegah penutupan berulang
        if ("DITUTUP".equals(closing.getStatus())) {
            result.put("success", false);
            result.put("message", "Tanggal tersebut sudah ditutup.");
            result.put("statusClosing", "DITUTUP");
            return result;
        }

        // Simpan penutupan
        closing.setStatus("DITUTUP");
        closing.setClosedBy(username);
        closing.setClosedAt(LocalDateTime.now());

        repository.save(closing);

        result.put("success", true);
        result.put("message", "Tutup harian berhasil dilakukan.");
        result.put("statusClosing", "DITUTUP");
        result.put("tanggal", tanggal);
        result.put("cabangId", cabangId);

        return result;
    }

   

    @Override
    @Transactional
    public Map<String, Object> reopen(
            LocalDate tanggal,
            Integer cabangId,
            String username,
            String reason) {

        Map<String, Object> result = new HashMap<>();
        if (tanggal == null || cabangId == null) {
            result.put("success", false);
            result.put("message", "Tanggal dan cabang wajib diisi.");
            return result;
        }

        if (reason == null || reason.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "Alasan buka kembali wajib diisi.");
            return result;
        }

        GlDailyClosing closing = repository
                .findByTanggalAndCabangId(tanggal, cabangId)
                .orElse(null);

        if (closing == null || !"DITUTUP".equals(closing.getStatus())) {
            result.put("success", false);
            result.put("message", "Tanggal tersebut belum ditutup.");
            return result;
        }

        closing.setStatus("TERBUKA");
        closing.setReopenedBy(username);
        closing.setReopenedAt(LocalDateTime.now());
        closing.setReopenReason(reason.trim());

        repository.save(closing);

        result.put("success", true);
        result.put("message", "Tanggal berhasil dibuka kembali.");
        result.put("statusClosing", "TERBUKA");
        result.put("tanggal", tanggal);
        result.put("cabangId", cabangId);

        return result;
    }
}
