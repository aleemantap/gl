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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.gl.exception.JournalValidationException;
import com.project.gl.helper.JournalCodeGenerator;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.Product;
import com.project.gl.model.enums.TransactionRejectionStatus;
import com.project.gl.model.entity.JournalId;
import com.project.gl.model.entity.JournalLineTemplate;
import com.project.gl.model.entity.GlAccountType;
import com.project.gl.model.entity.KodeTransaksi;
import com.project.gl.model.projection.ProductRuleComponentOption;
import com.project.gl.model.projection.ProductRuleOption;
import com.project.gl.model.request.GlItemRequest;
import com.project.gl.model.request.JournalAddRequest;
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
import com.project.gl.service.TransactionRejectionService;
import com.project.gl.service.JournalIdService;
import com.project.gl.service.JournalService;
import com.project.gl.model.response.ApiResponse;
import com.project.gl.model.response.JournaTransactionResponse;
import com.project.gl.model.response.JournalCodeOptionResponse;
import com.project.gl.service.JournalTransaksiService;
import com.project.gl.service.KodeTransaksiService;
import com.project.gl.repo.ProductRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
//operasional/input-jurnal-manual
@Controller
@RequestMapping("/operasional/input-jurnal-manual") 
public class InputJurnalManualController extends BaseController{

    @Autowired
    private CabangService cabangService;

    @Autowired
    private KodeTransaksiService kodeTransaksiService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private JournalService journalService;
    
    @Autowired 
    private  TransactionRejectionService transactionRejectionService;

    @Autowired
    private ObjectMapper objectMapper;


   
    @GetMapping
    public String index(Model model) {
        try {
       
            
            List<Cabang> cabangList = cabangService.getAll(); 
            model.addAttribute("cabangList", cabangList);
           
            List<KodeTransaksiResponse> kodeTransaksiList = kodeTransaksiService.getAll();
            model.addAttribute("kodeTransaksiList", kodeTransaksiList);         
            
            List<Product> prList = productService.getAll();
            model.addAttribute("productList", prList);         
        
            model.addAttribute("breadcrumbItems", new String[]{"OPERASIONAL", "INPUT JURNAL MANUAL"});
            
        } catch (Exception e) {
           // log.error("Error loading Buku Besar page: {}", e.getMessage());
            model.addAttribute("error", "Gagal memuat halaman: " + e.getMessage());
        }
        setActiveMenu(model, "input-jurnal-manual", "operasional");   
        return "operasional/input-jurnal-manual/index";
    }
    
    @PostMapping("/saveJurnal__2")
    public ResponseEntity<?> createJournal(@RequestBody JournalRequest request) {
        try {
            // Log data yang diterima
            System.out.println("Menerima data transaksi: " + request);
          
            JournalResponse response = journalService.createJournal(request);
            
            return ResponseEntity.ok(Map.of(
                "message", "Transaksi berhasil disimpan",
                "data", response
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "message", "Terjadi kesalahan: " + e.getMessage()
            ));
        }
    }


        @PostMapping("/saveJurnal")
        public ResponseEntity<?> createJournal(@RequestBody String rawPayload) {
        JournalRequest request = null;
        try {
                // =====================================================
                // 1. JSON ASLI DARI API
                // =====================================================
                request = objectMapper.readValue(rawPayload, JournalRequest.class);

                //  Debug: log semua field penting
                // System.out.println("=== DEBUG REQUEST ===");
                // System.out.println("typeTransaction: " + request.getTypeTransaction());
                // System.out.println("transactionCode: " + request.getTransactionCode());
                // System.out.println("transactionNo  : " + request.getTransactionNo());
                // System.out.println("branchCode     : " + request.getBranchCode());
                // System.out.println("=====================");


                // List<String> missingFields = new ArrayList<>();
                // if (request.getTransactionCode() == null || request.getTransactionCode().trim().isEmpty()) {
                //     missingFields.add("transactionCode tidak boleh kosong");
                // }
                // if (request.getTransactionNo() == null || request.getTransactionNo().trim().isEmpty()) {
                //     missingFields.add("transactionNo tidak boleh kosong");
                // }
                // if (request.getBranchCode() == null || request.getBranchCode().trim().isEmpty()) {
                //     missingFields.add("branchCode tidak boleh kosong");
                // }
                // if (!missingFields.isEmpty()) {
                //     throw new JournalValidationException(missingFields);
                // }

                JournalResponse response;

                if ("pembayaran".equalsIgnoreCase(request.getTypeTransaction())) {
                response = journalService.createJournal(request);
                } else if ("pencairan".equalsIgnoreCase(request.getTypeTransaction())) {
                response = journalService.createJournal(request);
                } else {
                throw new JournalValidationException(
                        List.of("typeTransaction harus 'pembayaran' atau 'pencairan', diterima: " 
                                + request.getTypeTransaction())
                );
                }

                return ResponseEntity.ok(response);

        } 
        catch (JsonProcessingException e) {
                // Error parsing — masalah di FE / format payload
                return ResponseEntity.badRequest().body(
                        JournalResponse.rejected(List.of(
                        "Format payload tidak valid: " + e.getOriginalMessage()
                        ))
                );
        } catch (JournalValidationException e) {
                String reason = String.join("; ", e.getErrors());


                


                transactionRejectionService.record(
                request != null && request.getOccurredAt() != null
                        ? request.getOccurredAt().toLocalDateTime()
                        : LocalDateTime.now(),
                request != null ? request.getTypeTransaction() : null,
                null, // cabangId
                request != null ? request.getBranchCode() : null,
                request != null ? request.getTransactionNo() : null,
                
                request != null && request.getTransactionCode() != null
                        ? request.getTransactionCode()
                        : "UNKNOWN",
                reason,
                rawPayload,
                TransactionRejectionStatus.REJECTED
                );

                return ResponseEntity
                        .unprocessableEntity()
                        .body(JournalResponse.rejected(e.getErrors()));

        } catch (Exception e) {



                transactionRejectionService.record(
                request != null && request.getOccurredAt() != null
                        ? request.getOccurredAt().toLocalDateTime()
                        : LocalDateTime.now(),
                request != null ? request.getTypeTransaction() : null,
                null,
                request != null ? request.getBranchCode() : null,
                request != null ? request.getTransactionNo() : null,
                
                request != null && request.getTransactionCode() != null
                        ? request.getTransactionCode()
                        : "UNKNOWN",
                e.getMessage(),
                rawPayload,
                TransactionRejectionStatus.REJECTED
                );

                return ResponseEntity
                        .unprocessableEntity()
                        .body(JournalResponse.rejected(List.of(e.getMessage())));
        }
        }

    @PostMapping("/saveJurnal2")
    public ResponseEntity<?> createJournal2(@RequestBody String rawPayload) {
        JournalRequest request = null;
        try {
            // =====================================================
            // 1. JSON ASLI DARI API
            // =====================================================
            request = objectMapper.readValue(
                    rawPayload,
                    JournalRequest.class
            );
            // =====================================================
            // 2. PROSES JOURNAL
            // =====================================================
            //JournalResponse response = journalService.createJournal(request);


             JournalResponse response = null;

          
            if (!"pembayaran".equalsIgnoreCase(request.getTypeTransaction())) {
                response = journalService.createJournal(request);
            }

            else if (!"pencairan".equalsIgnoreCase(request.getTypeTransaction())) {
                response = journalService.createJournal(request);
            }

            return ResponseEntity.ok(response);

        } catch (JournalValidationException e) {

        //            LocalDateTime transactionTime,
        //     String typeTransaction,
        //     Long cabangId,
        //     String branchCode,
        //     String transactionNo,
        //     String transactionCode,
        //     String reason,
        //     String payload,
        //     TransactionRejectionStatus status

            String reason = String.join("; ", e.getErrors());

             transactionRejectionService.record(
                    request != null
                            && request.getOccurredAt() != null
                            ? request.getOccurredAt()
                                    .toLocalDateTime()
                            : LocalDateTime.now(),
                   request != null
                            ? request.getTypeTransaction()
                            : null,
                    null,
                    request != null
                            ? request.getBranchCode()
                            : null,

                    request != null
                            ? request.getTransactionNo()
                            : null,

                    request != null
                            ? request.getTransactionCode()
                            : null,
                
                    

                    reason,

                    rawPayload,

                    TransactionRejectionStatus.REJECTED
            );
  //System.out.println("TESSSSSS=="+  request != null ? request.getTransactionCode(): null);
          
            return ResponseEntity
                    .unprocessableEntity()
                    .body(
                            JournalResponse.rejected(
                                    e.getErrors()
                            )
                    );

        } catch (Exception e) {

                
        //    System.out.println("TEE kaprok===" + (request != null ? request.getTransactionCode() : "null"));



            transactionRejectionService.record(

                    request != null
                            && request.getOccurredAt() != null
                            ? request.getOccurredAt()
                                    .toLocalDateTime()
                            : LocalDateTime.now(),

                   request != null
                            ? request.getTypeTransaction()
                            : null,

                    null,

                    request != null
                            ? request.getBranchCode()
                            : null,

                    request != null
                            ? request.getTransactionNo()
                            : null,

                    request != null
                            ? request.getTransactionCode()
                            : null,

                    e.getMessage(),

                    rawPayload,

                    TransactionRejectionStatus.REJECTED
            );

            return ResponseEntity
                    .unprocessableEntity()
                    .body(
                            JournalResponse.rejected(
                                    List.of(e.getMessage())
                            )
                    );
        }
    }
    
}
 


    // @PostMapping("/journals")
    // public ResponseEntity<JournalResponse> createJournal(@RequestBody JournalRequest request) {
    //     JournalResponse response = journalService.createJournal(request);
    //     return ResponseEntity.ok(response);
    // }