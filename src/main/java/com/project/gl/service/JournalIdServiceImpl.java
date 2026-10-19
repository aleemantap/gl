package com.project.gl.service;

import org.springframework.beans.factory.annotation.Autowired;
import com.project.gl.repo.JournalRepository;
import com.project.gl.repo.JournalTransactionRepository;
import com.project.gl.repo.JournalHeaderRepository;
import com.project.gl.repo.JournalIdRepository;
import com.project.gl.repo.JournalLineTemplateRepository;
import com.project.gl.helper.JournalCodeGenerator;
import com.project.gl.model.entity.JournalId;
import com.project.gl.model.entity.JournalLineTemplate;
import com.project.gl.model.entity.KodeTransaksi;
import com.project.gl.model.request.GlItemRequest;
import com.project.gl.model.request.JournalAddRequest;
import com.project.gl.model.request.JurnalComponentAddOne;
import com.project.gl.model.response.JournalIDResponse;
import com.project.gl.model.response.JournalLineTemplateResponse;
import com.project.gl.model.response.KodeTransaksiResponse;
import com.project.gl.model.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class JournalIdServiceImpl implements JournalIdService {
 
    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private JournalTransactionRepository transactionJournalRepository;

    @Autowired
    private JournalCodeGenerator codeGenerator;

    @Autowired
    private JournalLineTemplateRepository journalLineTemplateRepository;


    @Autowired
    private JournalHeaderRepository journalHeaderRepository;
    
    //@Autowired 
    //private JournalLineTemplateRepository journalLineTemplateRepository;
    
    @Autowired 
    private JournalIdRepository journalIdRepository;

    public JournalLineTemplateResponse createOneJlt(JurnalComponentAddOne request) {

        //insert
        JournalLineTemplate jlt = new JournalLineTemplate();
        jlt.setJournalId(request.getJurnalId());

        // ID GL dari request
        jlt.setGlAccountId(request.getNomorglcmpId());
        jlt.setProductRuleComponentId(request.getComponentRuleId());
       

        jlt.setDk(request.getDkcmp());
        // jlt.setLineOrder(lineOrder ??);
        List<JournalLineTemplate> existingList = journalLineTemplateRepository
            .findByJournalIdOrderByLineOrderAsc(request.getJurnalId());
        
        Integer lastLineOrder = existingList.isEmpty() ? 0 : 
            existingList.get(existingList.size() - 1).getLineOrder();
        int nextLineOrder = lastLineOrder + 1;
        jlt.setLineOrder(nextLineOrder);
      
        // jlt.setDescription(request.getDkcmp());
        jlt.setDescription("");
        jlt.setCashMovementType(request.getTypePergerakanCash());
        // line.setCreatedAt(LocalDateTime.now());
        // line.setCreatedBy("SYSTEM");
        // journalLineTemplateRepository.save(jlt);
        JournalLineTemplate savedJournal = journalLineTemplateRepository.save(jlt);

        JournalLineTemplateResponse response = new JournalLineTemplateResponse();
        response.setId(savedJournal.getId());
        response.setIsActive(savedJournal.getIsActive());
        return response;
    }
    
    // @Override
    public JournalIDResponse create(JournalAddRequest request) {
           
           // =====================================================
            // 1. INSERT HEADER journal_id
            // =====================================================

            JournalId journal = new JournalId();

            journal.setJournalCode(request.getJournalCode());
            journal.setDescription(request.getCatatan());
            journal.setIsActive(true);
            // journal.setCreatedBy("SYSTEM");
            // journal.setCreatedAt(LocalDateTime.now());
            JournalId savedJournal = journalRepository.save(journal);


            // =====================================================
            // 2. INSERT DETAIL journal_line_template
            // =====================================================

            int lineOrder = 1;

            for (GlItemRequest gl : request.getGlItems()) {

                JournalLineTemplate line = new JournalLineTemplate();

                // ID dari journal_id hasil INSERT
                line.setJournalId(savedJournal.getId());

                // ID GL dari request
                line.setGlAccountId(gl.getId());
                line.setProductRuleComponentId(gl.getComponentRuleId());
                // DEBIT -> D
                // CREDIT -> K 
                //String dk = convertDk(gl.getDk());

                line.setDk(gl.getDk());
                line.setLineOrder(lineOrder++);
                // line.setDescription(gl.getDk());
                line.setDescription(gl.getDk());
                line.setCashMovementType(gl.getCashMovementType());
                // line.setCreatedAt(LocalDateTime.now());
                // line.setCreatedBy("SYSTEM");
                journalLineTemplateRepository.save(line);
            }


            // =====================================================
            // 3. RESPONSE
            // =====================================================

            JournalIDResponse response = new JournalIDResponse();
            response.setId(savedJournal.getId());
            response.setJournalCode(savedJournal.getJournalCode());

            return response;
    }

    private String convertDk(String dk) {

        if (dk == null) {
            throw new IllegalArgumentException("DK tidak boleh kosong");
        }

        return switch (dk.toUpperCase()) {

            case "DEBIT", "D" -> "D";

            case "CREDIT", "KREDIT", "K" -> "K";

            default -> throw new IllegalArgumentException(
                    "DK tidak valid: " + dk
            );
        };
    }



     @Override
     public JournalIDResponse updateJournalId(Long id,  String description, Boolean isActive) {
        log.info("Updating Journal with id: {}", id);
        
        // Cari data berdasarkan ID
        JournalId existingJournal = journalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Journal dengan ID " + id + " tidak ditemukan"));
        
        // Validasi: Cek apakah journalCode sudah digunakan oleh data lain
        // if (journalCode != null && !journalCode.isEmpty()) {
        //     boolean exists = journalRepository.existsByJournalCodeAndIdNot(journalCode, id);
        //     if (exists) {
        //         throw new RuntimeException("Journal Code '" + journalCode + "' sudah digunakan!");
        //     }
        //     existingJournal.setJournalCode(journalCode);
        // }
        
        // Update description jika ada
        if (description != null) {
            existingJournal.setDescription(description);
        }
        
        // Update isActive jika ada
        if (isActive != null) {
            existingJournal.setIsActive(isActive);
        }
        
        // Update audit fields
        existingJournal.setUpdatedAt(LocalDateTime.now());
        existingJournal.setUpdatedBy("SYSTEM");
        
        // Save
        JournalId savedJournal = journalRepository.save(existingJournal);
        log.info("Journal with id: {} successfully updated", id);
        
        // Build response
        return buildResponse(savedJournal);
    }
    
    private JournalIDResponse buildResponse(JournalId journal) {
        JournalIDResponse response = new JournalIDResponse();
        response.setId(journal.getId());
        response.setJournalCode(journal.getJournalCode());
        response.setDescription(journal.getDescription());
        response.setIsActive(journal.getIsActive());
        //response.setUpdatedAt(journal.getUpdatedAt());
        //response.setUpdatedBy(journal.getUpdatedBy());
        return response;
    }

  


 


    // @Override
    // public PageResponse<JournalIDResponse> getAllWithFilter(
    //         int page, 
    //         int size, 
    //         String sortBy, 
    //         String orderDir,
    //         String kode,
    //         String namaTransaksi
    // ) {
       
    //     Sort.Direction direction = Sort.Direction.fromString(orderDir);
        
    //     Pageable pageable = PageRequest.of(page, size, direction, sortBy);        
    //     Page<JournalId> resultPage;
       
    //         resultPage = journalRepository.findAll(pageable);
        
    //     List<JournalIDResponse> content = resultPage.getContent().stream()
    //             .map(JournalIDResponse::new)
    //             .collect(Collectors.toList());
    //     return new PageResponse<>(
    //             content,
    //             resultPage.getNumber(),
    //             resultPage.getSize(),
    //             resultPage.getTotalElements(),
    //             resultPage.getTotalPages(),
    //             resultPage.isLast()
    //     );
    // }

  


   



    // @Override
    // @Transactional
    // public JournalIDResponse updateJournalId(Long id, JournalId journalIdDetails) {
    //     try {
    //         log.info("Updating journal ID with id: {}", id);
            
    //         // Find existing entity
    //         JournalId existingJournalId = journalRepository.findById(id)
    //                 .orElseThrow(() -> {
    //                     log.warn("Journal ID not found with id: {}", id);
    //                     return new RuntimeException("Journal ID not found with id: " + id);
    //                 });
            
    //         // Update fields
    //         if (journalIdDetails.getJournalCode() != null) {
    //             existingJournalId.setJournalCode(journalIdDetails.getJournalCode());
    //         }
    //         // if (journalIdDetails.getDebetGlAccountId() != null) {
    //         //     existingJournalId.setDebetGlAccountId(journalIdDetails.getDebetGlAccountId());
    //         // }
    //         // if (journalIdDetails.getCreditGlAccountId() != null) {
    //         //     existingJournalId.setCreditGlAccountId(journalIdDetails.getCreditGlAccountId());
    //         // }
    //         if (journalIdDetails.getDescription() != null) {
    //             existingJournalId.setDescription(journalIdDetails.getDescription());
    //         }
    //         if (journalIdDetails.getIsActive() != null) {
    //             existingJournalId.setIsActive(journalIdDetails.getIsActive());
    //         }
    //         if (journalIdDetails.getUpdatedBy() != null) {
    //             existingJournalId.setUpdatedBy(journalIdDetails.getUpdatedBy());
    //         }
            
    //         JournalId updated = journalRepository.save(existingJournalId);
    //         log.info("Successfully updated journal ID with id: {}", id);
            
    //         // Return the updated entity with details
    //         return getJournalIdById(updated.getId());
    //     } catch (RuntimeException e) {
    //         throw e;
    //     } catch (Exception e) {
    //         log.error("Error updating journal ID with id {}: {}", id, e.getMessage(), e);
    //         throw new RuntimeException("Failed to update journal ID: " + e.getMessage());
    //     }
    // }

    // @Override
    // @Transactional
    // public void deleteJournalId(Long id) {
    //     try {
    //         log.info("Deleting journal ID with id: {}", id);
            
    //         // Check if exists
    //         if (!journalIdRepository.existsById(id)) {
    //             log.warn("Journal ID not found with id: {}", id);
    //             throw new RuntimeException("Journal ID not found with id: " + id);
    //         }
            
    //         journalIdRepository.deleteById(id);
    //         log.info("Successfully deleted journal ID with id: {}", id);
    //     } catch (RuntimeException e) {
    //         throw e;
    //     } catch (Exception e) {
    //         log.error("Error deleting journal ID with id {}: {}", id, e.getMessage(), e);
    //         throw new RuntimeException("Failed to delete journal ID: " + e.getMessage());
    //     }
    // }


    @Override
    public Page<JournalIDResponse> getJournalIdsWithPagination(Pageable pageable) {
        try {
            // Ambil data dengan pagination
            Page<JournalIDResponse> journalPage = journalRepository.findAllJournalIdWithDetails(pageable);
            
            // Loop untuk setiap response dan set digunakanOleh
            for (JournalIDResponse response : journalPage.getContent()) {
                Long count = journalLineTemplateRepository.countByJournalId(response.getId());
                // response.setTotalAkun(count.intValue());
                response.setTotalAkun(count);
               //JournalLineTemplateRepository
            }
            return journalPage;
        } catch (Exception e) {
            // log.error("Error fetching journal IDs with pagination: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch journal IDs with pagination: " + e.getMessage());
        }
    }

    @Override
    public JournalIDResponse getJournalIdById(Long id) {
        try {
            return journalRepository.findJournalIdWithDetailsById(id)
                    .orElseThrow(() -> {
                        return new RuntimeException("Journal ID not found with id: " + id);
                    });
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            // log.error("Error fetching journal ID with id {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch journal ID: " + e.getMessage());
        }
    }

    @Override
    public JournalId generateRandomCode(JournalId journalId) {
        journalId.setJournalCode(codeGenerator.generateUniqueJournalCode());
        return journalRepository.save(journalId);
    }

    @Override
    public List<JournalLineTemplateResponse> getJournalLineTemplate(Long journalId) {

        return journalLineTemplateRepository
                .findDetailByJournalId(journalId)
                .stream()
                .map(line -> {
                    JournalLineTemplateResponse response =  new JournalLineTemplateResponse();

                        response.setId(line.getId());
                        response.setJournalId(line.getJournalId());
                        response.setGlAccountId(line.getGlAccountId());
                        response.setNomorGl(line.getNomorGl());
                        response.setNamaGlAccount(line.getNamaGlAccount());
                        response.setGlAccountType(line.getGlAccountType()); //(line.getGlAaccountType());
                        response.setDk(line.getDk());
                        response.setLineOrder(line.getLineOrder());
                        response.setDescription(line.getDescription());
                        response.setComponentName(line.getComponentName());
                        response.setComponentCode(line.getComponentCode());
                        response.setComponentId(line.getComponentId());
                        response.setIsActive(line.getIsActive()); 
                        response.setProductRuleId(line.getProductRuleId());
                        response.setCashMovementType(line.getCashMovementType());
             

                    return response;
                })
                .toList();
    }

    @Transactional
    public void hapusJournalId(Long id) {
        // 1. Validasi: masih dipakai di journalHeader?
        if (journalHeaderRepository.existsByJournalId(id)) {
            throw new IllegalStateException(
                "Journal ID tidak bisa dihapus karena masih dipakai di Journal Header");
        }

        // 2. Validasi: data ada?
        if (!journalIdRepository.existsById(id)) {
            throw new IllegalStateException("Journal ID dengan id " + id + " tidak ditemukan");
        }

        // 3. Hapus child dulu
        journalLineTemplateRepository.deleteByJournalId(id);

        // 4. Hapus parent
        journalIdRepository.deleteById(id);
    }
}

