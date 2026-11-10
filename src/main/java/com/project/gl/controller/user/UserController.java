package com.project.gl.controller.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.gl.model.response.ApiResponse;
import com.project.gl.model.response.PageResponse;
import com.project.gl.controller.BaseController;
import com.project.gl.model.entity.Cabang;
import com.project.gl.model.entity.UserGroup;
import com.project.gl.model.request.UserRequest;
import com.project.gl.model.response.UserResponse;
import com.project.gl.model.response.UserResponse2;
import com.project.gl.service.UserService;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import com.project.gl.service.CabangService;
import com.project.gl.service.GroupServie;




@Controller
@RequestMapping("/management/users")
public class UserController extends  BaseController{

        @Autowired
        private UserService userService;

        @Autowired
        private CabangService cabangService;

        @Autowired
        private GroupServie groupService;

    
        @GetMapping
        public String index(Model model) {
                
                List<Cabang> cabangList = cabangService.getAll(); 
                model.addAttribute("cabangList", cabangList); 
                List<UserGroup> userGroupList = groupService.getAll(); 
                model.addAttribute("roles", userGroupList); 
                model.addAttribute("breadcrumbItems", new String[]{"Console", "Management Pengguna"});
                setActiveMenu(model, "users", "management");  
                return "management/user/index";
        }

        @GetMapping("/create")
        public String showCreateForm(Model model) {

        
                List<Cabang> cabangList = cabangService.getAll(); 
                model.addAttribute("cabangList", cabangList); 
                List<UserGroup> userGroupList = groupService.getAll(); 
                model.addAttribute("roles", userGroupList); 
                model.addAttribute("title", "Form Pengguna Baru");     
                model.addAttribute("breadcrumbItems", new String[]{"Console", "Management Pengguna","Tambah Pengguna"});
                setActiveMenu(model, "users", "management");  
                return "management/user/form";
        }


        @GetMapping("/edit/{id}")
        public String showEditForm(Model model,@PathVariable Long id) {

                List<Cabang> cabangList = cabangService.getAll(); 
                model.addAttribute("cabangList", cabangList); 
                List<UserGroup> userGroupList = groupService.getAll(); 
                model.addAttribute("roles", userGroupList); 
                model.addAttribute("title", "Form Edit Pengguna");
                model.addAttribute("idUser", id);
                model.addAttribute("breadcrumbItems", new String[]{"Console", "Management Pengguna","Edit Pengguna"});
                setActiveMenu(model, "users", "management");  
                return "management/user/form";
        }

        /**
         * Get user by ID
         */
        @GetMapping("/get/{id}")
        public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
                try {
                UserResponse response = userService.getUserById(id);
                        return ResponseEntity.ok(
                                ApiResponse.success("User ditemukan", response)
                        );
                } catch (RuntimeException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(ApiResponse.error(e.getMessage()));
                }
        }

        @PostMapping("/save")
        public ResponseEntity<ApiResponse<UserResponse>> saveOrUpdateUser(@RequestBody UserRequest request) {
                try {
                        UserResponse response;

                        // Check if this is update or save
                        if (request.getId() != null && request.getId() > 0) {
                        // Update user
                        response = userService.updateUser(request);
                        return ResponseEntity.ok(
                                ApiResponse.success("User berhasil diupdate", response)
                        );
                        } else {
                        // Save new user
                        response = userService.saveUser(request);
                        return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.success("User berhasil dibuat", response));
                        }

                } catch (RuntimeException e) {
                        return ResponseEntity.badRequest()
                                .body(ApiResponse.error(e.getMessage()));
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error("Terjadi kesalahan: " + e.getMessage()));
                }
    }

    @GetMapping("/datatables")
    @ResponseBody
    public Map<String, Object> getDataTables(
            @RequestParam int draw,
            @RequestParam int start,
            @RequestParam int length,
            @RequestParam int orderColumn,
            @RequestParam String orderDir,
            // @RequestParam(required = false) String searchValue,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer userGroupId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer cabang
    
        ) {
         
        int page = start / length;
        int size = length;
        String sortBy = getSortColumn(orderColumn);
      
        PageResponse<UserResponse> response = userService.getAllUsers(page, size, sortBy, orderDir, name, userGroupId, status, cabang);
       
        //System.out.println("status"+status);
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
            case 2: return "email";
            // case 3: return "city";
            // case 4: return "phone";
            // case 5: return "email";
            default: return "id";
        }
    }

    @GetMapping("/export-excel")
    public void  exportExcel(
                @RequestParam(required = false) String ids,
                @RequestParam(required = false) String name,
                @RequestParam(required = false) Integer userGroupId,
                @RequestParam(required = false) String status,
                @RequestParam(required = false) Integer cabang,
                HttpServletResponse response
                )throws IOException  {
              
                // List<Cabang> items = userService.getAllWithFilterExport(ids,kode,cabang);

                List<UserResponse2> re = userService.findAllWithFiltersExport(ids, name, userGroupId, status, cabang);
     
               // 3. Set Response Header
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment; filename=users.xlsx");

                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("User");

                // Header Style
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);

                // Columns Definition
                String[] columns = {
                        "Nama",
                        "Email",
                        "Group",
                        "Cabang",
                        "Kode Cabang",
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
                        for (UserResponse2 item : re) {
                                Row row = sheet.createRow(rowNum++);
                                row.createCell(0).setCellValue(item.getName() != null ? item.getName() : "");
                                row.createCell(1).setCellValue(item.getEmail() != null ? item.getEmail() : "");
                                row.createCell(2).setCellValue(item.getUserGroup() != null ? item.getUserGroup() : "");
                                row.createCell(3).setCellValue(item.getCabangName() != null ? item.getCabangName() : "");
                                row.createCell(4).setCellValue(item.getCabangKode() != null ? item.getCabangKode() : "");
                                row.createCell(5).setCellValue(item.getActive() != null ? item.getActive() : "");
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


    
    @GetMapping("/non-aktive/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> nonAktiveById(@PathVariable Long id) {
       
        try {
                UserResponse response = userService.getUserById(id);
                return ResponseEntity.ok(
                ApiResponse.success("User ditemukan", response)
                );
        } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(e.getMessage()));
        }


    }
        
     /**
     * Menonaktifkan user (soft delete) - mengubah active menjadi 'N'
     */
     @PutMapping("/non-active/{id}")
     public ResponseEntity<ApiResponse<UserResponse>> nonActiveUser(@PathVariable Long id) {
        try {
                UserResponse response = userService.nonActiveUser(id);
                return ResponseEntity.ok(
                ApiResponse.success("User berhasil dinonaktifkan", response)
                );
        } catch (RuntimeException e) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error("Terjadi kesalahan: " + e.getMessage()));
        }
     }

        /**
         * Mengaktifkan kembali user - mengubah active menjadi 'Y'
         */
        @PutMapping("/active/{id}")
        public ResponseEntity<ApiResponse<UserResponse>> activeUser(@PathVariable Long id) {
                try {
                        UserResponse response = userService.activeUser(id);
                        return ResponseEntity.ok(
                        ApiResponse.success("User berhasil diaktifkan", response)
                        );
                } catch (RuntimeException e) {
                        return ResponseEntity.badRequest()
                                .body(ApiResponse.error(e.getMessage()));
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error("Terjadi kesalahan: " + e.getMessage()));
                }
        }

        /**
         * Toggle active status user (jika aktif -> non-aktif, jika non-aktif -> aktif)
         */
        @PutMapping("/toggle/{id}")
        public ResponseEntity<ApiResponse<UserResponse>> toggleUser(@PathVariable Long id) {
                try {
                        UserResponse user = userService.getUserById(id);
                        UserResponse response;
                        String message;

                        if ("Y".equals(user.getActive())) {
                        response = userService.nonActiveUser(id);
                        message = "User berhasil dinonaktifkan";
                        } else {
                        response = userService.activeUser(id);
                        message = "User berhasil diaktifkan";
                        }

                        return ResponseEntity.ok(ApiResponse.success(message, response));
                } catch (RuntimeException e) {
                        return ResponseEntity.badRequest()
                                .body(ApiResponse.error(e.getMessage()));
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error("Terjadi kesalahan: " + e.getMessage()));
                }
        }
   

    //change-password
    @GetMapping("/reset-password/{id}")
    public String passwordForm(Model model, @PathVariable Long id) {
              
        model.addAttribute("idUser", id);
        model.addAttribute("title", "Ganti Password");
        model.addAttribute("breadcrumbItems", new String[]{"Console", "Management Pengguna","Reset Password"});
        setActiveMenu(model, "users", "management");  
        return "profile/reset-password";
        //idUser
     
    }

}
