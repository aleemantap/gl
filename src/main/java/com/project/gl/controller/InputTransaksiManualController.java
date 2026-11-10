package com.project.gl.controller;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.gl.model.entity.Teller;
import com.project.gl.model.projection.KasPointTellerProjection;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.Product;
import com.project.gl.model.entity.KasPoint;
import com.project.gl.model.response.KodeTransaksiComboResponse;
import com.project.gl.model.response.KodeTransaksiResponse;
import com.project.gl.repo.ProductRepository;
import com.project.gl.service.CabangService;
import com.project.gl.service.KodeTransaksiService;
import com.project.gl.service.ProductService;
import com.project.gl.service.TellerService;
import com.project.gl.service.KasPointService;


@Controller
@RequestMapping("/operasional/input-transaksi-manual") 
public class InputTransaksiManualController extends BaseController {

    @Autowired
    private CabangService cabangService;

    @Autowired
    private KodeTransaksiService kodeTransaksiService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private TellerService tellerService;

    @Autowired
    private KasPointService kasPointService;


    @GetMapping
    public String index(Model model) {
        try {
       
            
            List<Cabang> cabangList = cabangService.getAll(); 
            model.addAttribute("cabangList", cabangList);

            List<Teller> kasirList = tellerService.getAll(); 
            model.addAttribute("kasirList", kasirList);
           
            List<KasPointTellerProjection> kasList = kasPointService.getAll(); 
            model.addAttribute("kasList", kasList);

           
            List<KodeTransaksiResponse> kodeTransaksiList = kodeTransaksiService.getAll();
            model.addAttribute("kodeTransaksiList", kodeTransaksiList);         
            
            //List<Product> prList = productService.getAll();
            //model.addAttribute("productList", prList);         
        
            model.addAttribute("breadcrumbItems", new String[]{"OPERASIONAL", "INPUT TRANSAKSI MANUAL"});
            
        } catch (Exception e) {
            model.addAttribute("error", "Gagal memuat halaman: " + e.getMessage());
        }
        setActiveMenu(model, "input-transaksi-manual", "operasional");   
        return "operasional/input-transaksi-manual/index";
    }

    @GetMapping("/search-jenis-transaksi")
    @ResponseBody
    public List<Map<String, Object>> getJenisTransaksi(
            @RequestParam("tipeTransaksi") String tipeTransaksi,
            @RequestParam("arahKas") String arahKas
            // @RequestParam("cabangId") Integer cabangId
        ) {

        List<Map<String, Object>> results = new ArrayList<>();

        List<KodeTransaksiComboResponse> transaksiList = kodeTransaksiService.getJenisTransaksi(
                        tipeTransaksi,
                        arahKas
                        
                );

        for (KodeTransaksiComboResponse transaksi : transaksiList) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", transaksi.getId());
            item.put("kode", transaksi.getKode());
            item.put("namaTransaksi", transaksi.getNamaTransaksi());
            item.put("cabangId", transaksi.getCabangId());
            results.add(item);
        }

        return results;
    }
}