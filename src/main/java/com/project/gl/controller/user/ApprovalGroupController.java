package com.project.gl.controller.user;
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
import com.project.gl.model.response.PageResponse;
import com.project.gl.model.entity.UserGroup;
import com.project.gl.model.response.UserResponse;
import com.project.gl.service.UserService;



@Controller
@RequestMapping("/management/group-approvals")
public class ApprovalGroupController {

      //management/group-approvals
    @Autowired
    private UserService userService;

    
    @GetMapping
    public String index(Model model) {
        
        model.addAttribute("breadcrumbItems", new String[]{"Console", "Management Aproval"});
        return "management/approval/index";
    }
    
}