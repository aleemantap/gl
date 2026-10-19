package com.project.gl.controller.parameter;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.gl.model.entity.CategoryTransaction;
import com.project.gl.model.entity.GlAccountType;
import com.project.gl.model.response.PageResponse;
import com.project.gl.repo.KodeTransaksiRepository;
import com.project.gl.repo.NomorGlAccountRepository;
import com.project.gl.service.TransactionCategoriService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/parameter/kategori-transaksi") 
public class KategoriTransaksiController {

    @Autowired
    private KodeTransaksiRepository kodeTransaksiRepository;

    @Autowired
    private TransactionCategoriService service;


    @GetMapping
    public String index(Model model) {
        model.addAttribute("breadcrumbItems", new String[]{"PARAMETER", "KATEGORI TRANSAKSI" });
        return "parameter/kategori-transaksi/index";
    }
    
    
    // Untuk DataTables
    @GetMapping("/datatables")
    @ResponseBody
    public Map<String, Object> getDataTables(
            @RequestParam int draw,
            @RequestParam int start,
            @RequestParam int length,
            @RequestParam int orderColumn,
            @RequestParam String orderDir,
            @RequestParam(required = false) String kode,
            @RequestParam(required = false) String name
           
    ) {
        int page = start / length;
        int size = length;
        String sortBy = getSortColumn(orderColumn);
        
        // if (searchValue != null && !searchValue.isEmpty()) {
        //     if ((name == null || name.isEmpty()) && (note == null || note.isEmpty())) {
        //         name = searchValue;
        //         note = searchValue;
        //     }
        // }
        
        PageResponse<CategoryTransaction> response = service.getAllWithFilter(
                page, size, sortBy, orderDir, kode, name);
        
        Map<String, Object> result = new HashMap<>();
        result.put("draw", draw);
        result.put("recordsTotal", response.getTotalElements());
        result.put("recordsFiltered", response.getTotalElements());
        result.put("data", response.getContent());
        
        return result;
    }

    // Untuk dropdown/select option
    // @GetMapping("/list")
    // public ResponseEntity<PageResponse<CategoryTransaction>> getList(
    //         @RequestParam(defaultValue = "0") int page,
    //         @RequestParam(defaultValue = "100") int size
    // ) {
    //     PageResponse<GlAccountType> response = service.getAllWithFilter(
    //             page, size, "name", "ASC", null);
    //     return ResponseEntity.ok(response);
    // }

//    @GetMapping("/gl-type-account/list-get-all")
//     public ResponseEntity<PageResponse<CategoryTransaction>> getAll(
//             @RequestParam(defaultValue = "0") int page,
//             @RequestParam(defaultValue = "10") int size,
//             @RequestParam(defaultValue = "id") String sortBy,
//             @RequestParam(defaultValue = "ASC") String orderDir,
//             @RequestParam(required = false) String name,
//             @RequestParam(required = false) String note
//     ) {
//         PageResponse<GlAccountType> response = service.getAllWithFilter(
//                 page, size, sortBy, orderDir, name);
//         return ResponseEntity.ok(response);
//     }

    @GetMapping("/get/{id}")
    public ResponseEntity<CategoryTransaction> getById(@PathVariable Integer id) {
        CategoryTransaction response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<CategoryTransaction> create(@RequestBody CategoryTransaction request) {
        CategoryTransaction response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CategoryTransaction> update(
            @PathVariable Integer id,
            @RequestBody CategoryTransaction request
    ) {
        CategoryTransaction response = service.update(id, request);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            // Cek apakah kategori digunakan di tabel lain (contoh: nomor_gl_account)
            Boolean isUsed = kodeTransaksiRepository.existsByCategoryId(id.longValue());
            
            if (isUsed) {
                // Kembalikan response dengan pesan error
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Kategori Transaksi tidak dapat dihapus karena sedang digunakan di tabel lain!");
            }
            
            // Jika tidak digunakan, hapus data
            service.delete(id);
            return ResponseEntity.ok().body("Data berhasil dihapus");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan: " + e.getMessage());
        }
    }

    private String getSortColumn(int orderColumn) {
        switch (orderColumn) {
            case 1: return "code";
            case 2: return "name";
            case 3: return "createdAt";
            case 4: return "updatedAt";
            default: return "id";
        }
    }

}
