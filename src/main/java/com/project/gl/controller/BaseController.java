package com.project.gl.controller;
import org.springframework.ui.Model;

public abstract class BaseController {

    protected void setActiveMenu(Model model, String menu, String group) {
        model.addAttribute("activeMenu", menu);
        model.addAttribute("activeGroup", group);
    }
    
}
