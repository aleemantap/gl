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
import com.project.gl.model.entity.TransactionRejection;
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
import com.project.gl.model.response.TransactionRejectionDetailDto;
import com.project.gl.model.response.TransactionRejectionDto;
import com.project.gl.model.response.UserResponse2;
import com.project.gl.service.CabangService;
import com.project.gl.service.GlAccountTypeService;
import com.project.gl.service.NomorGlAccountService;
import com.project.gl.service.ProductService;
import com.project.gl.service.TransactionRejectionService;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import com.project.gl.service.JournalIdService;
import com.project.gl.service.JournalService;
import com.project.gl.model.response.ApiResponse;
import com.project.gl.model.response.JournaTransactionResponse;
import com.project.gl.model.response.JournalCodeOptionResponse;
import com.project.gl.service.JournalTransaksiService;
import com.project.gl.service.KodeTransaksiService;
import com.project.gl.repo.ProductRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
@RequestMapping("/operasional/log-transaksi-ditolak") 
public class TransaksiDitolakController extends BaseController{
    

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
    private TransactionRejectionService service;

    @GetMapping
    public String index(Model model) {
        try {
            List<Cabang> cabangList = cabangService.getAll(); 
            model.addAttribute("cabangList", cabangList);           
            List<KodeTransaksiResponse> kodeTransaksiList = kodeTransaksiService.getAll();
            model.addAttribute("kodeTransaksiList", kodeTransaksiList);
            List<Product> prList = productService.getAll();
            model.addAttribute("productList", prList);
            model.addAttribute("statuses", TransactionRejectionStatus.values());
            model.addAttribute("breadcrumbItems", new String[]{"OPERASIONAL", "TRANSAKSI DITOLAK"});
            
        } catch (Exception e) {
           // log.error("Error loading Buku Besar page: {}", e.getMessage());
            model.addAttribute("error", "Gagal memuat halaman: " + e.getMessage());
        }
        setActiveMenu(model, "log-transaksi-ditolak", "operasional");        
        return "operasional/transaksi-ditolak/index";
    }


    @GetMapping("/data")
    @ResponseBody
    public List<TransactionRejectionDto> data() {

        return service.findAll();
    }


    @GetMapping("/payload/{id}")
    @ResponseBody
    public TransactionRejectionDetailDto payload(
            @PathVariable Long id) {

        return service.findDetail(id);
    }

     @GetMapping("/datatable")
    @ResponseBody
    public Map<String, Object> getDataTable(
            @RequestParam(defaultValue = "0") int draw,
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "10") int length,
            @RequestParam(defaultValue = "0") int orderColumn,
            @RequestParam(defaultValue = "desc") String orderDir,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {

        // Map column index to field name
        String[] columns = {
            "transactionTime", 
            "branchCode", 
            "transactionNo", 
            "transactionCode", 
            "rejectionReason", 
            "payload", 
            "status"
        };
        
        String sortColumn = columns[orderColumn];
        int page = start / length;

        Page<TransactionRejection> pageData = service.getTransactionRejections(
                status, fromDate, toDate, page, length, sortColumn, orderDir);

        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", pageData.getTotalElements());
        response.put("recordsFiltered", pageData.getTotalElements());
        response.put("data", pageData.getContent());

        return response;
    }

    private String mapColumnName(String columnName) {
        Map<String, String> columnMap = new HashMap<>();
        columnMap.put("transactionTime", "transactionTime");
        columnMap.put("branchCode", "branchCode");
        columnMap.put("transactionNo", "transactionNo");
        columnMap.put("transactionCode", "transactionCode");
        columnMap.put("rejectionReason", "rejectionReason");
        columnMap.put("payload", "payload");
        columnMap.put("status", "status");
        return columnMap.getOrDefault(columnName, "id");
    }

    
    @GetMapping("/export-excel")
    public void  exportExcel(
                @RequestParam(required = false) String ids,
                @RequestParam(required = false) String status,
                @RequestParam(required = false) String fromDate,
                @RequestParam(required = false) String toDate,
                HttpServletResponse response
                )throws IOException  {
              
              
                List<TransactionRejection> re = service.findAllWithFiltersExport(ids, status, fromDate, toDate);
     
               // 3. Set Response Header
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment; filename=log-transaksi-tolak.xlsx");

                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Log Transaksi Ditolak");

                // Header Style
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);

                // Columns Definition
                String[] columns = {
                        "Waktu Transaksi",
                        "Kode Cabang",
                        "Nama Cabang",
                        "No Transaksi",
                        "Kode Transaksi",
                        "Rejection Reason",
                        "Payload",
                        "Status"
                       
                };


                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < columns.length; i++) {
                        Cell cell = headerRow.createCell(i);
                        cell.setCellValue(columns[i]);
                        cell.setCellStyle(headerStyle);
                }
                int rowNum = 1;
             
                 if (re != null) {
                        for (TransactionRejection item : re) {
                                Row row = sheet.createRow(rowNum++);
                                String value = item.getTransactionTime() != null ? item.getTransactionTime().toString(): "";
                                // row.createCell(0).setCellValue(item.getTransactionTime() != null ? item.getTransactionTime() : "");
                                row.createCell(0).setCellValue(value);
                                row.createCell(1).setCellValue(item.getBranchCode() != null ? item.getBranchCode() : "");
                                row.createCell(2).setCellValue(item.getBranchCode() != null ? item.getBranchCode() : "");
                                row.createCell(3).setCellValue(item.getTransactionNo() != null ? item.getTransactionNo() : "");
                                row.createCell(4).setCellValue(item.getTransactionCode() != null ? item.getTransactionCode() : "");
                                row.createCell(5).setCellValue(item.getRejectionReason() != null ? item.getRejectionReason() : "");
                                row.createCell(6).setCellValue(item.getPayload() != null ? item.getPayload() : "");
                                // row.createCell(7).setCellValue(item.getStatus() != null ? item.getStatus() : "");
                                row.createCell(7).setCellValue(item.getStatus() != null ? item.getStatus().toString() : "");
                       }
                }

                // Auto-size kolom
                for (int i = 0; i < columns.length; i++) {
                        sheet.autoSizeColumn(i);
                }

                // Output ke Response Stream
                try (ServletOutputStream outputStream = response.getOutputStream()) {
                        workbook.write(outputStream);
                        workbook.close();
                }

    }


}
