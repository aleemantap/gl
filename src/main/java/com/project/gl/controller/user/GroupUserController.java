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
import org.yaml.snakeyaml.constructor.BaseConstructor;
import com.project.gl.controller.BaseController;
import com.project.gl.model.response.PageResponse;
import com.project.gl.model.entity.UserGroup;
import com.project.gl.model.response.UserResponse;
import com.project.gl.model.response.GroupResponse;
import com.project.gl.service.GroupServie;



@Controller
@RequestMapping("/management/groups")
public class GroupUserController extends BaseController {
    
    @Autowired
    private GroupServie groupServie;

    
    @GetMapping
    public String index(Model model) {
        
        model.addAttribute("breadcrumbItems", new String[]{"Console", "Management Group Users"});
        setActiveMenu(model, "groups", "management");  
        return "management/groups/index";
    }

    @GetMapping("/datatables")
    @ResponseBody
    public Map<String, Object> getDataTables(
            @RequestParam int draw,
            @RequestParam int start,
            @RequestParam int length,
            @RequestParam int orderColumn,
            @RequestParam String orderDir,
            @RequestParam(required = false) String searchValue,
            @RequestParam(required = false) String name
            
        ) {
         
        int page = start / length;
        int size = length; 
        String sortBy = getSortColumn(orderColumn);
      
        PageResponse<GroupResponse> response = groupServie.getAllGroups(
            page, size, sortBy, orderDir, name);
       
        
        Map<String, Object> result = new HashMap<>();
        result.put("draw", draw);
        result.put("recordsTotal", response.getTotalElements());
        result.put("recordsFiltered", response.getTotalElements());
        result.put("data", response.getContent());
        
        return result;
    }

     private String getSortColumn(int orderColumn) {
        switch(orderColumn) {
            case 1: return "name";
            //case 2: return "email";
            // case 3: return "city";
            // case 4: return "phone";
            // case 5: return "email";
            default: return "id";
        }
    }
}
