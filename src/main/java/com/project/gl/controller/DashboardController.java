package com.project.gl.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalSales", "$12,150");
        model.addAttribute("activeUsers", 111234);          
        // Kirim breadcrumb data
        // model.addAttribute("breadcrumbItems", new String[]{"Console", "Beranda Dashboard"});
        // model.addAttribute("currentPage", "Beranda Dashboard");
       
        // model.addAttribute("breadcrumbItems", new String[]{"Console", "Beranda Dashboard"});


        // model.addAttribute("breadcrumbItems", new String[]{"Console", "Beranda Dashboard"});
        // model.addAttribute("breadcrumbLinks", new String[]{"/dashboard", null});

        List<String> breadcrumbItems = Arrays.asList("Console","Dashboard");
        model.addAttribute("breadcrumbItems", breadcrumbItems);

        return "dashboard";
    }
}