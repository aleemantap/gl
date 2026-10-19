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
import com.project.gl.config.CustomUserPrincipal;
import com.project.gl.helper.JournalCodeGenerator;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.Product;
import com.project.gl.model.entity.ReversalRequest;
import com.project.gl.model.enums.ReversalRequestStatus;
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
import com.project.gl.model.response.ReversalRequestDetail;
import com.project.gl.model.response.ReversalRequestRow;
import com.project.gl.model.response.TransactionJournalSimpleResponse;
import com.project.gl.service.CabangService;
import com.project.gl.service.GlAccountTypeService;
import com.project.gl.service.NomorGlAccountService;
import com.project.gl.service.ProductService;
import com.project.gl.service.ReversalRequestService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.project.gl.repo.ProductRuleComponentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

//operasional/input-jurnal-manual
@Controller
@RequestMapping("/operasional/persetujuan") 
public class PersetujuanController extends BaseController {


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
    private ReversalRequestService reversalRequestService;


    @GetMapping
    public String index(Model model) {
        try {
            
         
                List<ReversalRequestRow> requests = reversalRequestService.findAllRows();

                long jumlahMenunggu = requests.stream()
                .filter(r ->
                        r.status() == ReversalRequestStatus.MENUNGGU
                )
                .count();

                model.addAttribute(
                        "requests",
                        requests
                );

                model.addAttribute(
                        "jumlahMenunggu",
                        jumlahMenunggu
                );
                model.addAttribute("breadcrumbItems", new String[]{"OPERASIONAL", "PERSETUJUAN"});
            
        } catch (Exception e) {
           // log.error("Error loading Buku Besar page: {}", e.getMessage());
            model.addAttribute("error", "Gagal memuat halaman: " + e.getMessage());
        }
        setActiveMenu(model, "persetujuan", "operasional");
        return "operasional/persetujuan/index";
    }


     /**
     * AJAX - approve reversal
     */
    @PostMapping("/approve/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> approve(
            @PathVariable Long id) {

        Map<String, Object> response =
                new HashMap<>();

        try {

            // Sementara user approver kita hardcode.
            // Nanti diganti dari Spring Security.
            String approvedBy = "SYSTEM";

            reversalRequestService.approve(
                    id,
                    approvedBy
            );

            response.put("success", true);
            response.put(
                    "message",
                    "Permintaan pembalikan berhasil disetujui."
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put("success", false);
            response.put(
                    "message",
                    e.getMessage()
            );

            return ResponseEntity
                    .badRequest()
                    .body(response);
        }
    }


    /**
     * AJAX - reject reversal
     */
    @PostMapping("/reject/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> reject(
            @PathVariable Long id,
            @RequestParam String rejectionReason) {

        Map<String, Object> response =
                new HashMap<>();

        try {

            String rejectedBy = "SYSTEM";

            reversalRequestService.reject(
                    id,
                    rejectedBy,
                    rejectionReason
            );

            response.put("success", true);
            response.put(
                    "message",
                    "Permintaan pembalikan berhasil ditolak."
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put("success", false);
            response.put(
                    "message",
                    e.getMessage()
            );

            return ResponseEntity
                    .badRequest()
                    .body(response);
        }
    }

        @GetMapping("/detail/{id}")
        @ResponseBody
        public ResponseEntity<?> detail(
                @PathVariable Long id) {

        try {

                ReversalRequestDetail detail =
                        reversalRequestService.findDetail(id);

                return ResponseEntity.ok(detail);

        } catch (Exception e) {

                return ResponseEntity.badRequest()
                        .body(
                        Map.of(
                                "success", false,
                                "message", e.getMessage()
                        )
                        );
        }
        }


        // @PostMapping("/request")
        // @ResponseBody
        // public ResponseEntity<Map<String, Object>> createRequest(
        //         @RequestParam Long journalId,
        //         @RequestParam String reason,
        //         @RequestParam(required = false) String description) {

        // try {

        //         String requestedBy = null; 
        //         // 2. Jika dari FE kosong/null, ambil dari login
        //         //if (requestedBy == null || requestedBy.trim().isEmpty()) {
        //         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //         if (auth != null && auth.isAuthenticated())
        //         {
        //                 Object principal = auth.getPrincipal();
        //                 if (principal instanceof CustomUserPrincipal) {
        //                         requestedBy = ((CustomUserPrincipal) principal).getUsername();
        //                 }
        //         }
        //         //}
        //         reversalRequestService.createRequest(
        //                 journalId,
        //                 requestedBy,
        //                 reason,
        //                 description
        //         );

        //         return ResponseEntity.ok(
        //                 Map.of(
        //                 "success", true,
        //                 "message",
        //                 "Permintaan pembalikan berhasil dikirim."
        //                 )
        //         );

        // } catch (Exception e) {

        //         return ResponseEntity.badRequest()
        //                 .body(
        //                 Map.of(
        //                         "success", false,
        //                         "message", e.getMessage()
        //                 )
        //                 );
        // }
        // }


        @PostMapping("/request")
        @ResponseBody
        public ResponseEntity<Map<String, Object>> createRequest(
                @RequestParam Long journalId,
                @RequestParam String reason,
                @RequestParam(required = false) String description) {

        try {
                String requestedBy = null; 
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated())
                {
                        Object principal = auth.getPrincipal();
                        if (principal instanceof CustomUserPrincipal) {
                                requestedBy = ((CustomUserPrincipal) principal).getUsername();
                        }
                }

                reversalRequestService.createRequest(
                        journalId,
                        requestedBy,
                        reason,
                        description
                );

                return ResponseEntity.ok(
                        Map.of(
                                "success", true,
                                "message", "Permintaan pembalikan berhasil dikirim."
                        )
                );

        } catch (Exception e) {

                return ResponseEntity.badRequest().body(
                        Map.of(
                                "success", false,
                                "message", e.getMessage()
                        )
                );
        }
        }
   
}
