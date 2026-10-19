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

import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.JournalId;
import com.project.gl.model.entity.GlAccountType;
import com.project.gl.model.entity.KodeTransaksi;
import com.project.gl.model.request.GlItemRequest;
import com.project.gl.model.request.JournalAddRequest;
import com.project.gl.model.request.JournalRequest;
import com.project.gl.model.request.TransactionJournalRequest;
import com.project.gl.model.response.JournalIDResponse;
import com.project.gl.model.response.KodeTransaksiResponse;
import com.project.gl.repo.JournalRepository;
import com.project.gl.model.response.PageResponse;
import com.project.gl.model.response.TransactionJournalSimpleResponse;
import com.project.gl.service.CabangService;
import com.project.gl.service.GlAccountTypeService;
import com.project.gl.service.JournalIdService;
import com.project.gl.model.response.ApiResponse;
import com.project.gl.model.response.JournaTransactionResponse;
import com.project.gl.model.response.JournalCodeOptionResponse;
import com.project.gl.service.JournalTransaksiService;
import com.project.gl.service.KodeTransaksiService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Controller
@RequestMapping("/parameter/mapping-gl-account-journal-id") 
public class MappingGlAccountJournalIdController {

    @Autowired
    private CabangService cabangService;

    @Autowired
    private GlAccountTypeService kategoriService;
    
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("breadcrumbItems", new String[]{"Parameter","Mapping  GL Account  Journal-ID" });
    

        List<Cabang> cabangList = cabangService.getAll(); 
        model.addAttribute("cabangList", cabangList);
        List<GlAccountType> kategoriList = kategoriService.getAll(); 
        model.addAttribute("kategoriList", kategoriList);

        return "parameter/mapping-gl/index";
    }
    
}
