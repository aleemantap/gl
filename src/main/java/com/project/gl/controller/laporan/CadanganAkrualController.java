package com.project.gl.controller.laporan;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties.BaseContainer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.project.gl.controller.BaseController;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.response.GlCkpnPreviewResponse;
import com.project.gl.service.CabangService;
import com.project.gl.service.GlCkpnPostingService;
import com.project.gl.service.GlCkpnService;
// import ch.qos.logback.core.model.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/laporan/cadangan-akrual")
// @RequiredArgsConstructor
public class CadanganAkrualController extends BaseController{

    @Autowired
    private GlCkpnService glCkpnService;
    
    @Autowired 
    private CabangService cabangService;

    @Autowired
    private GlCkpnPostingService glCkpnPostingService;

    @GetMapping
    public String index(Model model) {

        List<Cabang> cabangList = cabangService.getAll();
        model.addAttribute("cabangList", cabangList);

        // model.addAttribute("cabangList", cabangService.getAll());

        model.addAttribute("breadcrumbItems",
                new String[]{"LAPORAN", "Cadangan Akrual"});

        model.addAttribute("periode", YearMonth.now());

        setActiveMenu(model, "cadangan-akrual", "laporan");

        return "laporan/cadangan-akrual/index";
    }

    @PostMapping("/proses")
    public String proses(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth periode,
            @RequestParam Integer cabangId,
            @RequestParam String kodeCabang,
            Model model) {

        try {
            GlCkpnPreviewResponse preview =
                    glCkpnService.preview(
                            periode,
                            cabangId,
                            kodeCabang
                    );

            model.addAttribute("preview", preview);

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("periode", periode);
        model.addAttribute("cabangList", cabangService.getAll());
        setActiveMenu(model, "cadangan-akrual", "laporan");
        return "laporan/cadangan-akrual/index";
    }
   @PostMapping("/posting")
    public String posting(
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM")
            YearMonth periode,
            @RequestParam Integer cabangId,
            @RequestParam String kodeCabang,
            RedirectAttributes redirectAttributes) {

        try {
            // Cek kembali preview CKPN
            GlCkpnPreviewResponse preview =
                    glCkpnService.preview(periode, cabangId, kodeCabang);

            // Tidak ada selisih, tidak perlu posting
            if (!preview.isJurnalDiperlukan()) {
                redirectAttributes.addFlashAttribute(
                        "info",
                        "Saldo Cadangan Kerugian GL sudah sesuai dengan data Gadai. " +
                        "Tidak diperlukan jurnal penyesuaian."
                );

                return "redirect:/laporan/cadangan-akrual";
            }

            // Jika ada selisih, lanjutkan posting
            String journalNo = glCkpnPostingService.posting(
                    periode,
                    cabangId,
                    kodeCabang
            );

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Jurnal CKPN berhasil diposting: " + journalNo
            );

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/laporan/cadangan-akrual";
    }


    @PostMapping("/proses-ajax")
    @ResponseBody
    public ResponseEntity<?> prosesAjax(
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM") YearMonth periode,

            @RequestParam Integer cabangId,
            @RequestParam String kodeCabang) {

        try {
            GlCkpnPreviewResponse preview = glCkpnService.preview(periode, cabangId, kodeCabang);

            return ResponseEntity.ok(preview);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        }
    }
}