package com.project.gl.controller;


import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.GlPeriodClosing;
import com.project.gl.model.enums.GlPeriodClosingStatus;
import com.project.gl.model.response.GlPeriodClosingCheckResponse;
import com.project.gl.service.CabangService;
import com.project.gl.service.GlPeriodClosingService;
import org.springframework.util.StringUtils;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/operasional/period-closing-checker")
@RequiredArgsConstructor
public class GlPeriodClosingCheckerController extends BaseController {

    private final GlPeriodClosingService glPeriodClosingService;
    private final CabangService cabangService;

    /*
     * Halaman Tutup Buku
     */
    @GetMapping
    public String index(Model model) {
       
        List<Cabang> cabangList = cabangService.getAll();
        model.addAttribute("cabangList", cabangList);
        model.addAttribute("breadcrumbItems", new String[]{"OPERASIONAL", "Approval Tutup Buku"});
        setActiveMenu(model, "period-closing-checker", "operasional");
        return "operasional/period-closing-checker/index";
    }


      /*
     * Daftar pengajuan yang menunggu persetujuan
     */
    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<List<GlPeriodClosing>> list(
            @RequestParam Integer cabangId) {

        List<GlPeriodClosing> result = glPeriodClosingService.findAllByCabang(cabangId)
                        .stream()
                        .filter(item -> item.getStatus()
                                == GlPeriodClosingStatus.MENUNGGU_PERSETUJUAN)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    /*
     * Menyetujui tutup buku
     */
    @PostMapping("/approve")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> approve(
            @RequestParam Integer tahun,
            @RequestParam Integer bulan,
            @RequestParam Integer cabangId,
            Principal principal) {

        Map<String, Object> response = new HashMap<>();

        try {

            if (principal == null || !StringUtils.hasText(principal.getName())) {
                throw new IllegalStateException(
                        "Identitas user yang sedang login tidak tersedia."
                );
            }

            String username = principal.getName().trim();


            // String username = principal != null
            //         ? principal.getName()
            //         : "system";

            GlPeriodClosing result = glPeriodClosingService.approve(
                            tahun, bulan, cabangId, username);

            response.put("success", true);
            response.put("message", "Tutup buku berhasil disetujui.");
            response.put("data", result);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    /*
     * Menolak tutup buku
     */
    @PostMapping("/reject")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> reject(
            @RequestParam Integer tahun,
            @RequestParam Integer bulan,
            @RequestParam Integer cabangId,
            @RequestParam String rejectionReason,
            Principal principal) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (principal == null || !StringUtils.hasText(principal.getName())) {
                throw new IllegalStateException(
                        "Identitas user yang sedang login tidak tersedia."
                );
            }

            String username = principal.getName().trim();

            GlPeriodClosing result =
                    glPeriodClosingService.reject(
                            tahun,
                            bulan,
                            cabangId,
                            username,
                            rejectionReason
                    );

            response.put("success", true);
            response.put("message", "Pengajuan tutup buku berhasil ditolak.");
            response.put("data", result);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

        @GetMapping("/check")
        @ResponseBody
        public ResponseEntity<GlPeriodClosingCheckResponse> check(
                @RequestParam Integer tahun,
                @RequestParam Integer bulan,
                @RequestParam Integer cabangId
        ) {
        GlPeriodClosingCheckResponse result = glPeriodClosingService.check(tahun, bulan, cabangId);

        return ResponseEntity.ok(result);
        }

}