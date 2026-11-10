package com.project.gl.controller.user;

import java.util.ArrayList;

import com.project.gl.config.CustomUserPrincipal;
import com.project.gl.model.entity.User;
import com.project.gl.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.project.gl.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.project.gl.helper.SecurityUtil; 

@Controller
@RequestMapping("/setting/user")
public class ProfileController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder  passwordEncoder;


    @GetMapping("/profile")
    public String profileForm(Model model) {
        CustomUserPrincipal user = SecurityUtil.getCurrentUser();   
        User us = userRepository.findById(user.getId()).orElse(null);    
        String role =  user.getUserGroupName();
        //role = user.getUserGroupName()
       

        model.addAttribute("idUser", us.getId());
        model.addAttribute("name",  us.getName());
        model.addAttribute("email", us.getEmail());
        model.addAttribute("role", role);
        model.addAttribute("avatar", us.getAvatar());
        model.addAttribute("breadcrumbItems", new String[]{"Console", "Setting","User",user.getName()});
        return "profile/user";
    }

    @GetMapping("/change-password")
    public String passwordForm(Model model) {
        CustomUserPrincipal user = SecurityUtil.getCurrentUser();          
        model.addAttribute("idUser", user.getId());
        model.addAttribute("title", "Ganti Password");
        // List<Breadcrumb> breadcrumbs = new ArrayList<>();
        // breadcrumbs.add(new Breadcrumb(null, "Setting"));
        // breadcrumbs.add(new Breadcrumb("/setting/user/change-Password", "User"));
        // breadcrumbs.add(new Breadcrumb(null, "Change Password"));
        model.addAttribute("breadcrumbItems", new String[]{"Console", "Setting","User","Ganti Password"});
        //model.addAttribute("breadcrumbs", breadcrumbs);
        //idUser
        return "profile/password";
    }

    @PostMapping("/update-profile")
    @ResponseBody
    public ResponseEntity<?> updateUser(
            @RequestParam("id") String idStr,
            @RequestParam("email") String email,
            @RequestParam("name") String name,
            @RequestParam(value = "avatar", required = false) String avatarBase64) {
        
        try {
            Long id = Long.parseLong(idStr);
            
            // Cek user exists
            User existingUser = userRepository.findById(id).orElse(null);
            if (existingUser == null) {
                return ResponseEntity
                        .badRequest()
                        .body("User not found.");
            }
            
            // Cek email uniqueness
            if (userRepository.existsByEmailAndIdNot(email, id)) {
                return ResponseEntity
                        .badRequest()
                        .body("Email already exists.");
            }
            
            // Update data
            existingUser.setEmail(email);
            existingUser.setName(name);
            
            // Update avatar jika ada
            if (avatarBase64 != null && !avatarBase64.isEmpty()) {
                existingUser.setAvatar(avatarBase64);
            }
            
            userRepository.save(existingUser);
            
            return ResponseEntity.ok("Profile updated successfully!");
            
        } catch (NumberFormatException e) {
            return ResponseEntity
                    .badRequest()
                    .body("Invalid ID format: " + idStr);
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .badRequest()
                    .body("Failed to update profile: " + e.getMessage());
        }
    }

    @PostMapping("/change-password")
    @ResponseBody
    public ResponseEntity<?> changePassword(
            @RequestParam("id") String idStr,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword) {
        
        try {
            Long id = Long.parseLong(idStr);
            
            // 1. Cek user exists
            User user = userRepository.findById(id).orElse(null);
            if (user == null) {
                return ResponseEntity
                        .badRequest()
                        .body("User not found.");
            }
            
            // 2. Validasi password lama
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                return ResponseEntity
                        .badRequest()
                        .body("Old password is incorrect.");
            }
            
            // 3. Validasi password baru (minimal 8 karakter)
            if (newPassword.length() < 8) {
                return ResponseEntity
                        .badRequest()
                        .body("New password must be at least 8 characters.");
            }
            
            // 4. Validasi password baru tidak sama dengan lama
            if (passwordEncoder.matches(newPassword, user.getPassword())) {
                return ResponseEntity
                        .badRequest()
                        .body("New password cannot be the same as old password.");
            }
            
            // 5. Update password
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            
            return ResponseEntity.ok("Password changed successfully!");
            
        } catch (NumberFormatException e) {
            return ResponseEntity
                    .badRequest()
                    .body("Invalid ID format: " + idStr);
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .badRequest()
                    .body("Failed to change password: " + e.getMessage());
        }
    }


    @PostMapping("/reset-password")
    @ResponseBody
    public ResponseEntity<?> resetPassword(
            @RequestParam("id") String idStr,
            @RequestParam("newPassword") String newPassword) {
        
        try {
            Long id = Long.parseLong(idStr);
            
            // 1. Cek user exists
            User user = userRepository.findById(id).orElse(null);
            if (user == null) {
                return ResponseEntity
                        .badRequest()
                        .body("User not found.");
            }
            
          
            // 3. Validasi password baru (minimal 8 karakter)
            if (newPassword.length() < 8) {
                return ResponseEntity
                        .badRequest()
                        .body("New password must be at least 8 characters.");
            }
            
            // 4. Validasi password baru tidak sama dengan lama
            if (passwordEncoder.matches(newPassword, user.getPassword())) {
                return ResponseEntity
                        .badRequest()
                        .body("New password cannot be the same as old password.");
            }
            
            // 5. Update password
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            
            return ResponseEntity.ok("Password changed successfully!");
            
        } catch (NumberFormatException e) {
            return ResponseEntity
                    .badRequest()
                    .body("Invalid ID format: " + idStr);
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .badRequest()
                    .body("Failed to change password: " + e.getMessage());
        }
    }
}
