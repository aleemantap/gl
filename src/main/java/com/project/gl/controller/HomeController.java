package com.project.gl.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Menangani URL root (http://localhost:8081/)
    // @GetMapping("/")
    // public String index(Model model) {
    //     model.addAttribute("totalSales", "$12,450");
    //     model.addAttribute("activeUsers", 7000);
    //     return "dashboard"; 
    // }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

}