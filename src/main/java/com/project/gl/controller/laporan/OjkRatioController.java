package com.project.gl.controller.laporan;

import com.project.gl.model.request.OjkRatioCalculationRequest;
import com.project.gl.model.response.OjkRatioReportListDto;
import com.project.gl.controller.BaseController;
import com.project.gl.model.entity.OjkRatioReport;
import com.project.gl.model.enums.OjkRatioRegion;
import com.project.gl.service.OjkRatioService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/laporan/ojk-ratio")
@RequiredArgsConstructor
public class OjkRatioController extends BaseController{

    private final OjkRatioService ojkRatioService;

    /**
     * Halaman utama Rasio OJK
     */
    @GetMapping
    public String index(Model model) {

        model.addAttribute("periode", YearMonth.now().toString());

        model.addAttribute("regionOptions", List.of(
                OjkRatioRegion.KAB_KOTA,
                OjkRatioRegion.PROVINSI,
                OjkRatioRegion.NASIONAL
        ));

        model.addAttribute("reports",ojkRatioService.getAllReports());
        model.addAttribute("breadcrumbItems", new String[]{"LAPORAN", "OJK RATIO"});
        setActiveMenu(model, "ojk-ratio", "laporan");    

        return "laporan/ojk-ratio/index";
    }

    /**
     * AJAX: mengambil daftar laporan
     */
    @GetMapping("/data")
    @ResponseBody
    public ResponseEntity<?> getData() {

        List<OjkRatioReportListDto> reports =
                ojkRatioService.getAllReports()
                        .stream()
                        .map(report -> new OjkRatioReportListDto(
                                report.getId(),
                                report.getReportNo(),
                                report.getPeriode(),
                                report.getRegionScope(),
                                report.getTotalEquity(),
                                report.getPaidUpCapital(),
                                report.getStatus()
                        ))
                        .toList();

        return ResponseEntity.ok(reports);
    }

    /**
     * AJAX: kalkulasi rasio
     */
    @PostMapping("/calculate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> calculate(
            @RequestParam String periode,
            @RequestParam OjkRatioRegion regionScope
    ) {

        Map<String, Object> response = new HashMap<>();

        try {

            YearMonth yearMonth = YearMonth.parse(periode);

            OjkRatioCalculationRequest request =
                    new OjkRatioCalculationRequest();

            request.setPeriode(yearMonth.atEndOfMonth());
            request.setRegionScope(regionScope);

            OjkRatioReport report =
                    ojkRatioService.calculate(request);

            response.put("success", true);
            response.put("message", "Kalkulasi Rasio OJK berhasil.");
            response.put("reportNo", report.getReportNo());

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Halaman detail laporan Rasio OJK
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,Model model) {

        OjkRatioReport report = ojkRatioService.getReportById(id);

        model.addAttribute("report", report);
        model.addAttribute(
                "details",
                ojkRatioService.getReportDetails(id)
        );

        model.addAttribute(
                "breadcrumbItems",
                new String[]{"LAPORAN", "OJK RATIO", "DETAIL"}
        );

        setActiveMenu(model, "ojk-ratio", "laporan");

        return "laporan/ojk-ratio/detail";
    }
}