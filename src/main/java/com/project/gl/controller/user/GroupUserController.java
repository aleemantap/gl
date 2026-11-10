package com.project.gl.controller.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import com.project.gl.controller.BaseController;
import com.project.gl.model.response.PageResponse;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.UserGroup;
import com.project.gl.model.request.GroupRequest;
import com.project.gl.model.response.UserResponse;
import com.project.gl.model.response.ApiResponse;
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

    @GetMapping("/create")
    public String showCreateForm(Model model) {
              //
        model.addAttribute("title", "Form Group Baru");     
        model.addAttribute("breadcrumbItems", new String[]{"Console", "Management","Tambah Group"});
        setActiveMenu(model, "groups", "management");  
        return "management/groups/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(Model model,@PathVariable Long id) {
     
        model.addAttribute("title", "Form Edit Group");
        model.addAttribute("idGroup", id);
        model.addAttribute("breadcrumbItems", new String[]{"Console", "Management","Edit Group"});
        setActiveMenu(model, "groups", "management");  
        return "management/groups/form";
    }

        /**
     * Get user by ID
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponse<GroupResponse>> getUserById(@PathVariable Long id) {
            try {
            GroupResponse response = groupServie.getGroupById(id);
                    return ResponseEntity.ok(
                            ApiResponse.success("Group ditemukan", response)
                    );
            } catch (RuntimeException e) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error(e.getMessage()));
            }
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<GroupResponse>> saveOrUpdateUser(@RequestBody GroupRequest request) {
        try {
                GroupResponse response;

                // Check if this is update or save
                if (request.getId() != null && request.getId() > 0) {
                // Update user
                response = groupServie.updateGroup(request);
                return ResponseEntity.ok(
                        ApiResponse.success("Group berhasil diupdate", response)
                );
                } else {
                // Save new user
                response = groupServie.saveGroup(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success("Group berhasil dibuat", response));
                }

        } catch (RuntimeException e) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error("Terjadi kesalahan: " + e.getMessage()));
        }
    }

    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        try {
            groupServie.delete(id);
            return ResponseEntity.ok(
                ApiResponse.success("Group berhasil dihapus", null)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Terjadi kesalahan: " + e.getMessage()));
        }
}

}
