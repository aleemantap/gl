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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yaml.snakeyaml.constructor.BaseConstructor;

import com.project.gl.controller.BaseController;
import com.project.gl.helper.JournalCodeGenerator;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.NomorGlAccount;
import com.project.gl.model.response.BukuBesarPageResponse;
import com.project.gl.model.response.BukuBesarResponse;
import com.project.gl.model.response.JournalEntryResponse;
import com.project.gl.model.response.KodeTransaksiResponse;
import com.project.gl.model.response.NomorGelAccountResponse;
import com.project.gl.model.response.PageResponse;
import com.project.gl.model.response.SubLedgerReconciliationDto;
import com.project.gl.repo.NeracaSaldoRepository;
import com.project.gl.repo.NomorGlAccountRepository;
import com.project.gl.service.CabangService;
import com.project.gl.service.NomorGlAccountService;
import com.project.gl.service.SubLedgerReconciliationService;
import com.project.gl.service.JournalEntryService;
import com.project.gl.service.JournalIdService;
import com.project.gl.service.KodeTransaksiService;
import com.project.gl.service.NeracaSaldoService;
import com.project.gl.service.BukuBesarService;
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

@Controller
@RequestMapping("/laporan/rekonsiliasi-sub-ledger") 
public class RekonsiliasiSubLedgerController extends BaseController{

   

    @Autowired
    private NomorGlAccountRepository glAccountRepository;
    
    @Autowired
    private SubLedgerReconciliationService service;


    @GetMapping
    public String index(Model model) {
        setActiveMenu(model, "rekonsiliasi-sub-ledger", "laporan");  
        model.addAttribute("breadcrumbItems", new String[]{"LAPORAN", "Rekonsiliasi Sub Ledger"});
         model.addAttribute(  "accounts",glAccountRepository.findByIsActiveTrueOrderByNomorGlAsc());
        return "laporan/rekonsiliasi-sub-ledger/index";
    }


    @GetMapping("/data")
    @ResponseBody
    public SubLedgerReconciliationDto data(
            @RequestParam Integer glAccountId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        return service.reconcile(glAccountId,startDate,endDate);
        
    }
    
}
