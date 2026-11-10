package com.project.gl.controller;

import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.GlPeriodClosing;
import com.project.gl.model.response.GlPeriodClosingCheckResponse;
import com.project.gl.service.CabangService;
import com.project.gl.service.GlPeriodClosingService;

import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/operasional/period-closing")
@RequiredArgsConstructor
public class GlPeriodClosingController extends BaseController {

    private final GlPeriodClosingService glPeriodClosingService;
    private final CabangService cabangService;

    /*
     * Halaman Tutup Buku
     */
    @GetMapping
    public String index(Model model) {
       


        List<Cabang> cabangList = cabangService.getAll();
        model.addAttribute("cabangList", cabangList);
        model.addAttribute("breadcrumbItems", new String[]{"OPERASIONAL", "Tutup Buku"});
        setActiveMenu(model, "period-closing", "operasional");
        return "operasional/period-closing/index";
    }

    /*
     * Pemeriksaan persyaratan tutup buku
     */
    @GetMapping("/check")
    @ResponseBody
    public ResponseEntity<GlPeriodClosingCheckResponse> check(
            @RequestParam Integer tahun,
            @RequestParam Integer bulan,
            @RequestParam Integer cabangId
    ) {
        GlPeriodClosingCheckResponse result =
                glPeriodClosingService.check(tahun, bulan, cabangId);

        return ResponseEntity.ok(result);
    }

    /*
     * Konfirmasi jumlah transaksi Gadai yang belum dibukukan
     */
    @PostMapping("/confirm-pending-gadai")
    @ResponseBody
    public ResponseEntity<GlPeriodClosing> confirmPendingGadai(
            @RequestParam Integer tahun,
            @RequestParam Integer bulan,
            @RequestParam Integer cabangId,
            @RequestParam Integer pendingCount,
            @RequestParam(required = false) String note,
            Principal principal
    ) {

        GlPeriodClosing result =  glPeriodClosingService.confirmPendingGadai(
                        tahun,
                        bulan,
                        cabangId,
                        pendingCount,
                        getCurrentUsername(principal),
                        note
                );

        return ResponseEntity.ok(result);
    }

    /*
     * Finance mengajukan tutup buku
     */
    @PostMapping("/submit")
    @ResponseBody
    public ResponseEntity<GlPeriodClosing> submit(
            @RequestParam Integer tahun,
            @RequestParam Integer bulan,
            @RequestParam Integer cabangId,
            Principal principal
    ) {

        GlPeriodClosing result =
                glPeriodClosingService.submit(
                        tahun,
                        bulan,
                        cabangId,
                        getCurrentUsername(principal)
                );

        return ResponseEntity.ok(result);
    }

 
//     @PostMapping("/approve")
//     @ResponseBody
//     public ResponseEntity<GlPeriodClosing> approve(
//             @RequestParam Integer tahun,
//             @RequestParam Integer bulan,
//             @RequestParam Integer cabangId,
//             Principal principal
//     ) {

//         GlPeriodClosing result = glPeriodClosingService.approve(
//                         tahun,
//                         bulan,
//                         cabangId,
//                         getCurrentUsername(principal)
//                 );

//         return ResponseEntity.ok(result);
//     }

   
//     @PostMapping("/reject")
//     @ResponseBody
//     public ResponseEntity<GlPeriodClosing> reject(
//             @RequestParam Integer tahun,
//             @RequestParam Integer bulan,
//             @RequestParam Integer cabangId,
//             @RequestParam String rejectionReason,
//             Principal principal
//     ) {

//         GlPeriodClosing result =
//                 glPeriodClosingService.reject(
//                         tahun,
//                         bulan,
//                         cabangId,
//                         getCurrentUsername(principal),
//                         rejectionReason
//                 );

//         return ResponseEntity.ok(result);
//     }

    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<List<GlPeriodClosing>> list(@RequestParam Integer cabangId) {
        return ResponseEntity.ok(glPeriodClosingService.findAllByCabang(cabangId));
    }


    private String getCurrentUsername(Principal principal) {
		if (principal == null || principal.getName() == null
				|| principal.getName().isBlank()) {
			throw new IllegalStateException(
					"Identitas user yang sedang login tidak tersedia."
			);
		}

		return principal.getName().trim();
	}

	
    
}