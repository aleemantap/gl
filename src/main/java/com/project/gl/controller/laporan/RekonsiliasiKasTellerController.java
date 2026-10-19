package com.project.gl.controller.laporan;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.poi.ss.usermodel.*;
import java.nio.file.Path;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.project.gl.config.CustomUserPrincipal;
import com.project.gl.controller.BaseController;
import com.project.gl.helper.JournalCodeGenerator;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.NomorGlAccount;
import com.project.gl.model.response.BukuBesarPageResponse;
import com.project.gl.model.response.BukuBesarResponse;
import com.project.gl.model.response.CashReconciliationDto;
import com.project.gl.model.response.JournalEntryResponse;
import com.project.gl.model.response.KodeTransaksiResponse;
import com.project.gl.model.response.NomorGelAccountResponse;
import com.project.gl.model.response.PageResponse;
import com.project.gl.repo.NeracaSaldoRepository;
import com.project.gl.service.CabangService;
import com.project.gl.service.NomorGlAccountService;
import com.project.gl.service.JournalEntryService;
import com.project.gl.service.JournalIdService;
import com.project.gl.service.KodeTransaksiService;
import com.project.gl.service.NeracaSaldoService;
import com.project.gl.service.BukuBesarService;
import com.project.gl.service.RekonsiliasiKasTellerService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@Controller
@RequestMapping("/laporan/rekonsiliasi-kas-teller") 
public class RekonsiliasiKasTellerController extends BaseController {
    @Autowired
    private NomorGlAccountService nomorGlAccountService;

    @Autowired
    private CabangService cabangService;

    @Autowired
    private NeracaSaldoRepository neracaSaldoRepository;

    @Autowired
    private NeracaSaldoService neracaSaldoService;

    @Autowired
    private  RekonsiliasiKasTellerService reconciliationService;

    @GetMapping
    public String index(
            @RequestParam(required = false) Long tellerUserId,
            @RequestParam(required = false) String kasPointCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate date,
            @RequestParam(required = false) String openingBalance,
            @RequestParam(required = false) String physicalBalance,
            Model model) {

           
            // Ubah nama variabel penampung hasil parse
            BigDecimal openingBalanceDecimal = parseRupiahToBigDecimal(openingBalance);
            BigDecimal physicalBalanceDecimal = parseRupiahToBigDecimal(physicalBalance);

        if (tellerUserId != null
                && kasPointCode != null
                && date != null
                && openingBalanceDecimal != null) {

            CashReconciliationDto result = reconciliationService.getReconciliation(
                            tellerUserId,
                            kasPointCode,
                            date,
                            openingBalanceDecimal,
                            physicalBalanceDecimal
                    );
            model.addAttribute("result", result);
        }
       model.addAttribute("breadcrumbItems", new String[]{"LAPORAN", "Rekonsiliasi Kas Teller"});
       setActiveMenu(model, "rekonsiliasi-kas-teller", "laporan");   
       return "laporan/rekonsiliasi-kas-teller/index";
    }

    @PostMapping("/save")
    public String saveReconciliation(
            @RequestParam Long tellerUserId,
            @RequestParam String kasPointCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,                    
            @RequestParam BigDecimal openingBalance,
            @RequestParam BigDecimal physicalBalance,
            RedirectAttributes redirectAttributes) {

        try {

            String reconciledBy = null; // nnati bisa di kondisikan dengan variable dari FE/View

            // 2. Jika dari FE kosong/null, ambil dari login
            if (reconciledBy == null || reconciledBy.trim().isEmpty()) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated()) {
                    Object principal = auth.getPrincipal();
                    if (principal instanceof CustomUserPrincipal) {
                        reconciledBy = ((CustomUserPrincipal) principal).getUsername();
                    }
                }
            }


            reconciliationService.saveReconciliation(
                    tellerUserId,
                    kasPointCode,
                    date,
                    openingBalance,
                    physicalBalance,
                    reconciledBy
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Rekonsiliasi berhasil disimpan"
            );

        } catch (IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Gagal menyimpan rekonsiliasi"
            );
        }

        return "redirect:/laporan/rekonsiliasi-kas-teller";
    }

    private BigDecimal parseRupiahToBigDecimal(String rupiahString) {
        if (rupiahString == null) {
            return BigDecimal.ZERO;
        }
        String cleanString = rupiahString.replaceAll("[^0-9]", "");
        if (cleanString.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(cleanString);
    }
}
