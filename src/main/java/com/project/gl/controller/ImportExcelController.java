package com.project.gl.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.gl.helper.JournalCodeGenerator;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.Product;
import com.project.gl.model.entity.JournalId;
import com.project.gl.model.entity.JournalLineTemplate;
import com.project.gl.model.entity.GlAccountType;
import com.project.gl.model.entity.KodeTransaksi;
import com.project.gl.model.projection.ProductRuleComponentOption;
import com.project.gl.model.projection.ProductRuleOption;
import com.project.gl.model.request.GlItemRequest;
import com.project.gl.model.request.ImportRequest;
import com.project.gl.model.request.JournalRequest;
import com.project.gl.model.request.JurnalComponentAddOne;
import com.project.gl.model.request.TransactionJournalRequest;
import com.project.gl.model.response.JournalIDResponse;
import com.project.gl.model.response.JournalLineTemplateResponse;
import com.project.gl.model.response.JournalResponse;
import com.project.gl.model.response.KodeTransaksiResponse;
import com.project.gl.model.response.NomorGelAccountResponse;
import com.project.gl.repo.JournalLineTemplateRepository;
import com.project.gl.repo.JournalRepository;
import com.project.gl.model.response.PageResponse;
import com.project.gl.model.response.TransactionJournalSimpleResponse;
import com.project.gl.service.CabangService;
import com.project.gl.service.GlAccountTypeService;
import com.project.gl.service.NomorGlAccountService;
import com.project.gl.service.ProductService;
import com.project.gl.service.JournalIdService;
import com.project.gl.service.JournalService;
import com.project.gl.model.response.ApiResponse;
import com.project.gl.model.response.JournaTransactionResponse;
import com.project.gl.model.response.JournalCodeOptionResponse;
import com.project.gl.service.JournalTransaksiService;
import com.project.gl.service.KodeTransaksiService;
import com.project.gl.repo.ProductRepository;
import com.project.gl.service.ExcelImportService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.project.gl.repo.ProductRuleComponentRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
// import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.*;
//operasional/input-jurnal-manual
@Controller
@RequestMapping("/import") 
public class ImportExcelController {

    private final JournalService journalService;
    private final ExcelImportService excelImportService;

    // Constructor Injection
    public ImportExcelController(JournalService journalService, ExcelImportService excelImportService) {
        this.journalService = journalService;
        this.excelImportService = excelImportService;
    }


      /**
     * Endpoint untuk import data dari Excel
     */
    @PostMapping("/posting-excel")
    public ResponseEntity<?> importJournal(@RequestBody ImportRequest importRequest) {
        try {
            System.out.println("Menerima import data: " + importRequest.getFileName());
            System.out.println("Jumlah data: " + importRequest.getData().size());
            
            // Konversi data Excel ke JournalRequest
            List<JournalRequest> journalRequests = excelImportService.convertToJournalRequests(importRequest.getData());
            
            // Proses setiap data
            List<JournalResponse> responses = new ArrayList<>();
            List<Map<String, Object>> errors = new ArrayList<>();
            
            int successCount = 0;
            int errorCount = 0;
            
            for (int i = 0; i < journalRequests.size(); i++) {
                try {
                    JournalRequest request = journalRequests.get(i);
                    JournalResponse response = journalService.createJournal(request);
                    responses.add(response);
                    successCount++;
                } catch (Exception e) {
                    errorCount++;
                    errors.add(Map.of(
                        "row", i + 2,
                        "error", e.getMessage()
                    ));
                    System.err.println("Error pada row " + (i + 2) + ": " + e.getMessage());
                }
            }
            
            // Return response dengan statistik
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Import selesai");
            response.put("totalData", journalRequests.size());
            response.put("successCount", successCount);
            response.put("errorCount", errorCount);
            response.put("errors", errors);
            response.put("responses", responses);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "message", "Terjadi kesalahan saat import: " + e.getMessage()
            ));
        }
    }


    // @PostMapping("/posting-excel-pembayaran")
    // public ResponseEntity<?> importPembayaran(@RequestBody ImportRequest importRequest) {
    //     try {
    //         System.out.println("Menerima import data: " + importRequest.getFileName());
    //         System.out.println("Jumlah data: " + importRequest.getData().size());
    //         System.out.println("Type Excel: " + importRequest.getTypeExcel());
    //         // Konversi data Excel ke JournalRequest
    //         List<JournalRequest> journalRequests = excelImportService.convertToJournalRequestsPembayaran(importRequest.getData());
            
    //         // Proses setiap data
    //         List<JournalResponse> responses = new ArrayList<>();
    //         List<Map<String, Object>> errors = new ArrayList<>();
            
    //         int successCount = 0;
    //         int errorCount = 0;
            
    //         for (int i = 0; i < journalRequests.size(); i++) {
    //             try {
    //                 JournalRequest request = journalRequests.get(i);
    //                 JournalResponse response = journalService.createJournal(request);
    //                 responses.add(response);
    //                 successCount++;
    //             } catch (Exception e) {
    //                 errorCount++;
    //                 errors.add(Map.of(
    //                     "row", i + 2,
    //                     "error", e.getMessage()
    //                 ));
    //                 System.err.println("Error pada row " + (i + 2) + ": " + e.getMessage());
    //             }
    //         }
            
    //         // Return response dengan statistik
    //         Map<String, Object> response = new HashMap<>();
    //         response.put("message", "Import selesai");
    //         response.put("totalData", journalRequests.size());
    //         response.put("successCount", successCount);
    //         response.put("errorCount", errorCount);
    //         response.put("errors", errors);
    //         response.put("responses", responses);
            
    //         return ResponseEntity.ok(response);
            
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
    //             "message", "Terjadi kesalahan saat import: " + e.getMessage()
    //         ));
    //     }
    // }


    // @PostMapping("/posting-excel-pencairan")
    // public ResponseEntity<?> importPencairan(@RequestBody ImportRequest importRequest) {
    //     try {
    //         System.out.println("Menerima import data: " + importRequest.getFileName());
    //         System.out.println("Jumlah data: " + importRequest.getData().size());
    //         System.out.println("Type Excel: " + importRequest.getTypeExcel());
    //         // Konversi data Excel ke JournalRequest
    //         List<JournalRequest> journalRequests = excelImportService.convertToJournalRequestsPencairan(importRequest.getData());
            
    //         // Proses setiap data
    //         List<JournalResponse> responses = new ArrayList<>();
    //         List<Map<String, Object>> errors = new ArrayList<>();
            
    //         int successCount = 0;
    //         int errorCount = 0;
            
    //         for (int i = 0; i < journalRequests.size(); i++) {
    //             try {
    //                 JournalRequest request = journalRequests.get(i);
    //                 JournalResponse response = journalService.createJournal(request);
    //                 responses.add(response);
    //                 successCount++;
    //             } catch (Exception e) {
    //                 errorCount++;
    //                 errors.add(Map.of(
    //                     "row", i + 2,
    //                     "error", e.getMessage()
    //                 ));
    //                 System.err.println("Error pada row " + (i + 2) + ": " + e.getMessage());
    //             }
    //         }
            
    //         // Return response dengan statistik
    //         Map<String, Object> response = new HashMap<>();
    //         response.put("message", "Import selesai");
    //         response.put("totalData", journalRequests.size());
    //         response.put("successCount", successCount);
    //         response.put("errorCount", errorCount);
    //         response.put("errors", errors);
    //         response.put("responses", responses);
            
    //         return ResponseEntity.ok(response);
            
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
    //             "message", "Terjadi kesalahan saat import: " + e.getMessage()
    //         ));
    //     }
    // }

    /**
     * Alternative: Upload file Excel langsung (bukan JSON)
     */
    @PostMapping("/upload-excel")
    public ResponseEntity<?> uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "message", "File tidak boleh kosong"
                ));
            }
            
            // Baca file Excel
            List<Map<String, Object>> excelData = excelImportService.readExcelFile(file.getInputStream());
            
            // Konversi ke JournalRequest
            List<JournalRequest> journalRequests = excelImportService.convertToJournalRequests(excelData);
            
            // Proses data
            List<JournalResponse> responses = new ArrayList<>();
            List<Map<String, Object>> errors = new ArrayList<>();
            
            int successCount = 0;
            int errorCount = 0;
            
            for (int i = 0; i < journalRequests.size(); i++) {
                try {
                    JournalRequest request = journalRequests.get(i);
                    JournalResponse response = journalService.createJournal(request);
                    responses.add(response);
                    successCount++;
                } catch (Exception e) {
                    errorCount++;
                    errors.add(Map.of(
                        "row", i + 2,
                        "error", e.getMessage()
                    ));
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Import selesai");
            response.put("totalData", journalRequests.size());
            response.put("successCount", successCount);
            response.put("errorCount", errorCount);
            response.put("errors", errors);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "message", "Gagal upload file: " + e.getMessage()
            ));
        }
    }



}
