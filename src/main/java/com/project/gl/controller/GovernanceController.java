package com.project.gl.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.Keputusan;
import com.project.gl.model.entity.NeracaSaldoSubmission;
import com.project.gl.model.enums.KeputusanStatus;
import com.project.gl.model.response.KodeTransaksiResponse;
import com.project.gl.model.response.NeracaSaldoRow;
import com.project.gl.service.CabangService;
import com.project.gl.service.KeputusanService;
import com.project.gl.service.KodeTransaksiService;
import com.project.gl.service.NeracaSaldoService2;

@Controller
@RequestMapping("/governance")
public class GovernanceController extends BaseController {

    @Autowired
    private CabangService cabangService;

    @Autowired
    private KodeTransaksiService kodeTransaksiService;

    @Autowired
    private NeracaSaldoService2 neracaSaldoService;


     @Autowired
    private  KeputusanService keputusanService;

    // =========================================
    // VALIDASI NERACA SALDO - LIST
    // =========================================

    @GetMapping("/validasi-neraca-saldo")
    public String validasi(
            @RequestParam(required = false) String periode,
            Model model
    ) {
        try {
            model.addAttribute(
                    "breadcrumbItems",
                    new String[]{"OPERASIONAL", "VALIDASI NERACA"}
            );

            LocalDate periodeDate;
            if (periode == null || periode.isBlank()) {
                periodeDate = LocalDate.now().withDayOfMonth(1);
            } else {
                periodeDate = LocalDate.parse(periode + "-01");
            }

            long total = neracaSaldoService.countTotal(periodeDate);
            long valid = neracaSaldoService.countValid(periodeDate);
            long waiting = neracaSaldoService.countWaiting(periodeDate);

            double progress = total == 0
                    ? 0
                    : ((double) valid / total) * 100;

            model.addAttribute("periode", periodeDate);
            model.addAttribute(
                    "periodeInput",
                    periodeDate.toString().substring(0, 7)
            );
            model.addAttribute(
                    "submissions",
                    neracaSaldoService.getQueueWithCabang(periodeDate)
            );
            model.addAttribute(
                    "recentValidations",
                    neracaSaldoService.getRecentValidationRows()
            );
            model.addAttribute("totalUnit", total);
            model.addAttribute("validUnit", valid);
            model.addAttribute("waiting", waiting);
            model.addAttribute("progress", Math.round(progress));

        } catch (Exception e) {
            model.addAttribute(
                    "error",
                    "Gagal memuat halaman: " + e.getMessage()
            );
        }

        setActiveMenu(model, "validasi-neraca-saldo", "governance");
        return "governance/validasi-neraca-saldo/index";
    }

    // =========================================
    // VALIDASI NERACA SALDO - REVIEW
    // =========================================

    @GetMapping("/validasi-neraca-saldo/review/{id}")
    public String review(@PathVariable Long id, Model model) {

        NeracaSaldoSubmission submission = neracaSaldoService.findById(id);

        List<NeracaSaldoRow> rows = neracaSaldoService.getNeracaSaldo(
                submission.getUnitId(),
                submission.getPeriode()
        );

        BigDecimal totalDebit = rows.stream()
                .map(NeracaSaldoRow::getDebit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredit = rows.stream()
                .map(NeracaSaldoRow::getCredit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal selisih = totalDebit.subtract(totalCredit).abs();

        model.addAttribute("submission", submission);
        model.addAttribute("rows", rows);
        model.addAttribute("totalDebit", totalDebit);
        model.addAttribute("totalCredit", totalCredit);
        model.addAttribute("selisih", selisih);

        if (selisih.compareTo(BigDecimal.ZERO) != 0) {
            model.addAttribute(
                    "balanceError",
                    "Total Debit dan Kredit tidak seimbang."
            );
        }

        model.addAttribute(
                "breadcrumbItems",
                new String[]{"GOVERNANCE", "VALIDASI NERACA SALDO", "REVIEW"}
        );

        setActiveMenu(model, "validasi-neraca-saldo", "governance");
        return "governance/validasi-neraca-saldo/review";
    }

    // =========================================
    // VALIDASI NERACA SALDO - APPROVE / REJECT
    // =========================================

    @PostMapping("/validasi-neraca-saldo/validate/{id}")
    public String validate(
            @PathVariable Long id,
            @RequestParam(required = false) String note
    ) {
        neracaSaldoService.validate(id, "Admin Pusat", note);
        return "redirect:/governance/validasi-neraca-saldo";
    }

    @PostMapping("/validasi-neraca-saldo/reject/{id}")
    public String reject(
            @PathVariable Long id,
            @RequestParam(required = false) String note
    ) {
        neracaSaldoService.reject(id, "Admin Pusat", note);
        return "redirect:/governance/validasi-neraca-saldo";
    }

    // =========================================
    // VALIDASI NERACA SALDO - DETAIL
    // =========================================

    @GetMapping("/validasi-neraca-saldo/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {

        NeracaSaldoSubmission submission = neracaSaldoService.findById(id);

        List<NeracaSaldoRow> rows = neracaSaldoService.getNeracaSaldo(
                submission.getUnitId(),
                submission.getPeriode()
        );

        BigDecimal totalDebit = rows.stream()
                .map(NeracaSaldoRow::getDebit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredit = rows.stream()
                .map(NeracaSaldoRow::getCredit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal selisih = totalDebit.subtract(totalCredit).abs();

        model.addAttribute("submission", submission);
        model.addAttribute("rows", rows);
        model.addAttribute("totalDebit", totalDebit);
        model.addAttribute("totalCredit", totalCredit);
        model.addAttribute("selisih", selisih);
        model.addAttribute(
                "breadcrumbItems",
                new String[]{"GOVERNANCE", "VALIDASI NERACA SALDO", "DETAIL"}
        );

        setActiveMenu(model, "validasi-neraca-saldo", "governance");
        return "governance/validasi-neraca-saldo/detail";
    }

    // =========================================
    // KEPUTUSAN TERBUKA
    // =========================================

    @GetMapping("/keputusan-terbuka")
    public String keputusan(Model model) {
        try {
     

                model.addAttribute(
                        "keputusanList",
                        keputusanService.findAll()
                );

                model.addAttribute(
                        "statusList",
                        KeputusanStatus.values()
                );

      

        } catch (Exception e) {
            model.addAttribute(
                    "error",
                    "Gagal memuat halaman: " + e.getMessage()
            );
        }
        model.addAttribute(
                    "breadcrumbItems",
                    new String[]{"GOVERNANCE", "KEPUTUSAN TERBUKA"}
            );
        setActiveMenu(model, "keputusan-terbuka", "governance");
        return "governance/keputusan-terbuka/index";
    }

    @GetMapping("/keputusan-terbuka/form")
    public String form(Model model) {

        Keputusan keputusan = new Keputusan();

        keputusan.setStatus(KeputusanStatus.TERBUKA);

        model.addAttribute("keputusan", keputusan);

        model.addAttribute(
                "statusList",
                KeputusanStatus.values()
        );

        return "/governance/keputusan-terbuka/form";
    }


    @PostMapping("/keputusan-terbuka/save")
    public String save(@ModelAttribute Keputusan keputusan,RedirectAttributes redirectAttributes) {

        try {

                keputusanService.save(keputusan);

                redirectAttributes.addFlashAttribute(
                        "successMessage",
                        "Keputusan berhasil disimpan."
                );

        } catch (Exception e) {

                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "Gagal menyimpan keputusan: " + e.getMessage()
                );
        }

        return "redirect:/governance/keputusan-terbuka";
     }

    @GetMapping("/keputusan-terbuka/edit/{id}")
    public String edit(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "keputusan",
                keputusanService.findById(id)
        );

        model.addAttribute(
                "statusList",
                KeputusanStatus.values()
        );

       // return "keputusan/form";
        return "governance/keputusan-terbuka/form";
    }

    @GetMapping("/keputusan-terbuka/delete/{id}")
    public String delete(
            @PathVariable Long id) {

        keputusanService.deleteById(id);

        return "redirect:/keputusan-terbuka";
    }
}