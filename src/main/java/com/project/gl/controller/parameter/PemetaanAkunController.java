package com.project.gl.controller.parameter;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.gl.controller.BaseController;
import com.project.gl.helper.JournalCodeGenerator;
import com.project.gl.model.entity.Cabang;
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
import com.project.gl.model.response.KodeTransaksiResponse;
import com.project.gl.model.response.NomorGelAccountResponse;
import com.project.gl.repo.JournalHeaderRepository;
import com.project.gl.repo.JournalLineTemplateRepository;
import com.project.gl.repo.JournalRepository;
import com.project.gl.model.response.PageResponse;
import com.project.gl.model.response.TransactionJournalSimpleResponse;
import com.project.gl.service.CabangService;
import com.project.gl.service.GlAccountTypeService;
import com.project.gl.service.NomorGlAccountService;

import com.project.gl.service.JournalIdService;
import com.project.gl.model.response.ApiResponse;
import com.project.gl.model.response.JournaTransactionResponse;
import com.project.gl.model.response.JournalCodeOptionResponse;
import com.project.gl.service.JournalTransaksiService;
import com.project.gl.service.KodeTransaksiService;
import com.project.gl.repo.ProductRepository;
import java.time.LocalDateTime;
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

@Controller
@RequestMapping("/parameter/pemetaan-akun") 
public class PemetaanAkunController  extends  BaseController{


    @Autowired
    private JournalIdService service;  

    @Autowired
    private JournalTransaksiService transactionJournalService;

    @Autowired
    private CabangService cabangService;

    @Autowired
    private GlAccountTypeService kategoriService;

    @Autowired
    private KodeTransaksiService kodeTransaksiService;

    @Autowired
    private JournalRepository journalIdRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JournalCodeGenerator codeGenerator;

    @Autowired
    private ProductRuleComponentRepository productRuleComponentRepository;


    @Autowired
    private NomorGlAccountService nomorGlAccountService;

    @Autowired
    private JournalLineTemplateRepository journalLineTemplateRepository;

    @Autowired 
    private JournalHeaderRepository journalHeaderRepository;


    
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("breadcrumbItems", new String[]{"Parameter","Pemetaan Akun (JournalId)" });
        // List<Cabang> cabangList = cabangService.getAll(); 
        // model.addAttribute("cabangList", cabangList);        
        List<ProductRuleOption> productRules = productRepository.findProductRuleOptions();
        model.addAttribute("productRules", productRules);
        List<Cabang> cabangList = cabangService.getAll(); 
        model.addAttribute("cabangList", cabangList);
        List<GlAccountType> kategoriList = kategoriService.getAll(); 
        model.addAttribute("kategoriList", kategoriList);
        List<KodeTransaksiResponse> kodeTransaksiList = kodeTransaksiService.getAll();
        model.addAttribute("kodeTransaksiList", kodeTransaksiList);         
        List<NomorGelAccountResponse> nogel = nomorGlAccountService.getAll();
        model.addAttribute("noAccGelList", nogel);       
        setActiveMenu(model, "pemetaan-akun", "parameter");            
        return "parameter/pemetaanAkun/index";
    }

     // TAB 1 
    @GetMapping("/datatable-transaction-journal")
    @ResponseBody
    public Map<String, Object> getDataTables(
            @RequestParam int draw,
            @RequestParam int start,
            @RequestParam int length,
            @RequestParam(required = false) String cabangId
    ) {
        int page = start / length;
        int size = length;
        
        Page<JournaTransactionResponse> response = transactionJournalService.getAllWithFilter(
                page, size, cabangId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("draw", draw);
        result.put("recordsTotal", response.getTotalElements());
        result.put("recordsFiltered", response.getTotalElements());
        result.put("data", response.getContent());
        
        return result;
    }

    @PostMapping("/add")
    public ResponseEntity<?> saveJournal(@RequestBody JournalAddRequest request) {
        JournalIDResponse response = service.create(request);
         //return ResponseEntity.ok(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/add-one")
    public ResponseEntity<?> saveComponent(@RequestBody JurnalComponentAddOne request) {
        JournalLineTemplateResponse response = service.createOneJlt(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/datatables")
    @ResponseBody
    public Map<String, Object> getDataTables(
            @RequestParam int draw,
            @RequestParam int start,
            @RequestParam int length,
            @RequestParam int orderColumn,
            @RequestParam String orderDir,
            @RequestParam(required = false) String searchValue) {
        
        try {
            // Hitung page dari start dan length
            int page = start / length;
            
            // Tentukan kolom sorting
            String sortBy = getSortColumn2(orderColumn);
            
            // Buat Pageable dengan sorting
            Sort.Direction direction = Sort.Direction.fromString(orderDir);
            Pageable pageable = PageRequest.of(page, length, Sort.by(direction, sortBy));
            
            // Panggil service dengan pagination
            Page<JournalIDResponse> journalPage = service.getJournalIdsWithPagination(pageable);
            
            // Siapkan response untuk DataTables
            Map<String, Object> result = new HashMap<>();
            result.put("draw", draw);
            result.put("recordsTotal", journalPage.getTotalElements());
            result.put("recordsFiltered", journalPage.getTotalElements());
            result.put("data", journalPage.getContent());
            
            return result;
        } catch (Exception e) {
            // log.error("Error fetching DataTables data: {}", e.getMessage(), e);
            
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("draw", draw);
            errorResult.put("recordsTotal", 0);
            errorResult.put("recordsFiltered", 0);
            errorResult.put("data", new Object[0]);
            errorResult.put("error", e.getMessage());
            
            return errorResult;
        }
    }

    // Method untuk mapping order column ke field entity
    private String getSortColumn2(int orderColumn) {
        switch (orderColumn) {
            case 1:  // Kolom ID (index 1)
                return "id";
            case 2:  // Kolom journalCode (index 2)
                return "journalCode";
            case 3:  // Kolom description (index 3)
                return "description";
            case 4:  // Kolom totalAkun (index 4)
                return "totalAkun";
            default:
                return "id";  // Default ke ID
        }
    }

    @GetMapping("/journal-line-template/{journalId}")
    public ResponseEntity<?> getJournalLineTemplate(@PathVariable Long journalId) {
        return ResponseEntity.ok(service.getJournalLineTemplate(journalId));
    }

  

    @GetMapping("/journal-code/list")
    public ResponseEntity<List<JournalCodeOptionResponse>> getJournalCodeList() {

        List<JournalCodeOptionResponse> data =
                journalIdRepository
                        .findAllByIsActiveTrueOrderByJournalCodeAsc()
                        .stream()
                        .map(j -> new JournalCodeOptionResponse(
                                j.getId(),
                                j.getJournalCode(),
                                j.getDescription()
                        ))
                        .toList();

        return ResponseEntity.ok(data);
    }

    @PostMapping("/journal-trk/add")
    public ResponseEntity<ApiResponse<Void>> createTrJurnal(@RequestBody TransactionJournalRequest request) {
        try {
            transactionJournalService.create(request);
            return ResponseEntity.ok(ApiResponse.success("Data berhasil disimpan", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/journal-trk-get/{id}")
    public ResponseEntity<ApiResponse<TransactionJournalSimpleResponse>> getById(@PathVariable Long id) {
        try {
            TransactionJournalSimpleResponse response = transactionJournalService.getById(id);
            return ResponseEntity.ok(ApiResponse.success("Data ditemukan", response));
        } catch (Exception e) {
           
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

  
    // @PostMapping("/jurnal-update/{id}")
    // public ResponseEntity<JournalIDResponse> updateJournalId(
    //         @PathVariable Long id, 
    //         @RequestBody JournalId journalId) {
    //     return ResponseEntity.ok(service.updateJournalId(id, journalId));
    // }

    @PostMapping("/jurnal-update/{id}")
    public ResponseEntity<ApiResponse<JournalIDResponse>> updateJournalId(
            @PathVariable Long id,
            @RequestParam String description,
            @RequestParam Boolean isActive) {
        try {
            JournalIDResponse response = service.updateJournalId(id, description, isActive);
            return ResponseEntity.ok(ApiResponse.success("Journal berhasil diupdate", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }



    @PostMapping("/jlt-update/{id}")
    public ResponseEntity<ApiResponse<Void>> updateTrJurnal(
            @PathVariable Long id,
            @RequestParam Long selectComponent,
            @RequestParam String dk,
            @RequestParam Boolean isActive,
            @RequestParam String cashMovementType
        ) {
        try {
            // Cari data berdasarkan ID
            JournalLineTemplate existingData = journalLineTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Data dengan ID " + id + " tidak ditemukan"));
            
            // Update field
            existingData.setProductRuleComponentId(selectComponent);
            existingData.setDk(dk);
            existingData.setIsActive(isActive);
            existingData.setCashMovementType(cashMovementType);
            
            
            // Update audit fields
            //existingData.setUpdatedAt(LocalDateTime.now());
            //existingData.setUpdatedBy("SYSTEM");
            
            // Save ke database
            journalLineTemplateRepository.save(existingData);
            
            return ResponseEntity.ok(ApiResponse.success("Data berhasil diupdate", null));
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Terjadi kesalahan: " + e.getMessage()));
        }
    }


    @PostMapping("/jlt-template-hapus/{id}")
    public ResponseEntity<ApiResponse<Void>> jltTemplateHapus(@PathVariable Long id) {
        try {
        
            JournalLineTemplate existingData = journalLineTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Data dengan ID " + id + " tidak ditemukan"));
            journalLineTemplateRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Data berhasil dihapus", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/journal-id-hapus/{id}")
    public ResponseEntity<ApiResponse<Void>> journalIdHapus(@PathVariable Long id) {
        try {
            service.hapusJournalId(id);
            return ResponseEntity.ok(ApiResponse.success("Data berhasil dihapus", null));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/journal-trk/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTrJurnal(@PathVariable Long id) {
        try {
            // harus nya ada pengeccekan data dengan tabel2
            transactionJournalService.delete(id);
            return ResponseEntity.ok(ApiResponse.success("Data berhasil dihapus", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }


    // @GetMapping("/get/{id}")
    // public ResponseEntity<JournalIDResponse> getJournalIdById(@PathVariable Long id) {
    //     return ResponseEntity.ok(service.getJournalIdById(id));
    // }



    /* *

    @GetMapping("/datatables-12xx")
    @ResponseBody
    public Map<String, Object> getDataTables(
            @RequestParam int draw,
            @RequestParam int start,
            @RequestParam int length,
            @RequestParam int orderColumn,
            @RequestParam String orderDir,
            @RequestParam(required = false) String searchValue,
            @RequestParam(required = false) String kode,
            @RequestParam(required = false) String namaTransaksi
    ) {
        int page = start / length;
        int size = length;
        String sortBy = getSortColumn(orderColumn);
        
        if (searchValue != null && !searchValue.isEmpty()) {
            if ((kode == null || kode.isEmpty()) && (namaTransaksi == null || namaTransaksi.isEmpty())) {
                kode = searchValue;
                namaTransaksi = searchValue;
            }
        }
        
        PageResponse<JournalIDResponse> response = service.getAllWithFilter(
                page, size, sortBy, orderDir, kode, namaTransaksi);
        
        Map<String, Object> result = new HashMap<>();
        result.put("draw", draw);
        result.put("recordsTotal", response.getTotalElements());
        result.put("recordsFiltered", response.getTotalElements());
        result.put("data", response.getContent());
        
        return result;
    }

    private String getSortColumn(int orderColumn) {
        switch (orderColumn) {
            case 1: return "kode";
            case 2: return "namaTransaksi";
            case 3: return "createdAt";
            case 4: return "updatedAt";
            default: return "id";
        }
    }

    

    private boolean isEmptyParam(String param) {
        return param == null || param.isEmpty();
    }

  
 


    

   

    */

    
    @GetMapping("/generate-random-id")
    public ResponseEntity<Map<String, Object>> generateJournalCode() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Hanya generate kode, tidak insert ke database
            String randomCode = codeGenerator.generateUniqueJournalCode();
            
            response.put("success", true);
            response.put("randomId", randomCode);
            response.put("message", "ID berhasil digenerate");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("randomId", null);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/options-rule-component/{productRuleId}")
    public ResponseEntity<List<ProductRuleComponentOption>> getOptions(@PathVariable Long productRuleId) {
        List<ProductRuleComponentOption> data = productRuleComponentRepository.findOptionsByProductRuleId(productRuleId);
        return ResponseEntity.ok(data);
    }
} 
