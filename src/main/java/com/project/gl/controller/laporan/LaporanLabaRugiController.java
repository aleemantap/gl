package com.project.gl.controller.laporan;

import java.time.YearMonth;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import com.project.gl.controller.BaseController;
import com.project.gl.model.response.LaporanLabaRugiDetailResponse;
import com.project.gl.model.response.LaporanLabaRugiResponse;
import com.project.gl.service.CabangService;
import com.project.gl.service.LaporanLabaRugiService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/laporan/laba-rugi")
@RequiredArgsConstructor
public class LaporanLabaRugiController extends  BaseController {

    private final LaporanLabaRugiService laporanLabaRugiService;
    private final CabangService cabangService;

    @GetMapping
    public String index(
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM")
            YearMonth periode,
            @RequestParam(required = false)
            Long cabangId,
            Model model) {

        if (periode == null) {
            periode = YearMonth.now();
        }

        List<LaporanLabaRugiResponse> laporan = laporanLabaRugiService.getLaporan(periode, cabangId);

        model.addAttribute("periode", periode);
        model.addAttribute("cabangId", cabangId);
        model.addAttribute("laporan", laporan);

        model.addAttribute("cabangList", cabangService.getAll());
        setActiveMenu(model, "laba-rugi", "laporan");
        return "laporan/laba-rugi/index";
    }

    @GetMapping("/detail/{kodePos}")
    @ResponseBody
    public ResponseEntity<List<LaporanLabaRugiDetailResponse>> detail(
            @PathVariable String kodePos,
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM")
            YearMonth periode,
            @RequestParam(required = false)
            Long cabangId) {

        List<LaporanLabaRugiDetailResponse> details =
                laporanLabaRugiService.getDetailJurnal(
                        kodePos,
                        periode,
                        cabangId
                );

        return ResponseEntity.ok(details);
    }
}