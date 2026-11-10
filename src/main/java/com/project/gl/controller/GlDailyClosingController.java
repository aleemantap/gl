
package com.project.gl.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.project.gl.service.GlDailyClosingService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/operasional/tutup-harian")
@RequiredArgsConstructor
public class GlDailyClosingController extends BaseController {

    private final GlDailyClosingService dailyClosingService;

    /**
     * Halaman Tutup Harian
     */
    @GetMapping
    public String index(Model model) {

        // Sementara untuk menampilkan desain.
        // Nanti tanggal dan cabang diambil dari konteks user login.
        LocalDate tanggal = LocalDate.now();

        model.addAttribute("tanggal", tanggal);
        model.addAttribute("cabangId", 2);
        model.addAttribute("kodeCabang", "002");
        model.addAttribute("namaCabang", "Tangsel");
        model.addAttribute("breadcrumbItems", new String[]{"OPERASIONAL", "Tutup Harian"});
        setActiveMenu(model, "tutup-harian", "operasional");
        return "operasional/tutup-harian/index";
    }

    /**
     * Data pemeriksaan melalui AJAX
     */
    @GetMapping("/data")
    @ResponseBody
    public ResponseEntity<?> getData(
            @RequestParam LocalDate tanggal,
            @RequestParam Integer cabangId) {

        return ResponseEntity.ok(
                dailyClosingService.check(tanggal, cabangId)
        );
    }

    /**
     * Tutup Harian melalui AJAX
     */
        @PostMapping("/close")
        @ResponseBody
        public ResponseEntity<?> close(
                @RequestParam LocalDate tanggal,
                @RequestParam Integer cabangId) {

                String username = getCurrentUsername();
                Map<String, Object> response = dailyClosingService.close(tanggal, cabangId, username);
                return ResponseEntity.ok(response);
        }       

        /**
         * Buka Kembali melalui AJAX
         */
        @PostMapping("/reopen")
        @ResponseBody
        public ResponseEntity<?> reopen(
                @RequestParam LocalDate tanggal,
                @RequestParam Integer cabangId,
                @RequestParam String reason) {

                String username = getCurrentUsername();
                Map<String, Object> response = dailyClosingService.reopen(
                        tanggal, cabangId, username, reason
                );

                return ResponseEntity.ok(response);
        }

        private String getCurrentUsername() {

                var authentication =
                        org.springframework.security.core.context.SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                return authentication != null
                        ? authentication.getName()
                        : "SYSTEM";
        }

}
