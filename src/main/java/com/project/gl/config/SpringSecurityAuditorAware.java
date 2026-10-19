package com.project.gl.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
// @Component
@Component("springSecurityAuditorAware")
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    // @Override
    // public Optional<String> getCurrentAuditor() {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    //     if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
    //         return Optional.of("SYSTEM"); // Default jika tanpa context login
    //     }

    //     return Optional.of(authentication.getName());
    // }

    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            // Jika tidak ada authentication atau tidak login, return "SYSTEM"
            if (authentication == null || !authentication.isAuthenticated()) {
                //log.info("No authenticated user, using SYSTEM");
                return Optional.of("SYSTEM");
            }
            
            Object principal = authentication.getPrincipal();
            
            if (principal instanceof CustomUserPrincipal) {
                CustomUserPrincipal currentUser = (CustomUserPrincipal) principal;
                String username = currentUser.getUsername();
                // log.info("Current auditor: {}", username);
                return Optional.of(username != null ? username : "SYSTEM");
            }
            
        } catch (Exception e) {
            // log.error("Error getting current auditor", e);
        }
        
        return Optional.of("SYSTEM");
    }

    
}


// @Component
// public class SpringSecurityAuditorAware implements AuditorAware<String> {

//     @Override
//     public Optional<String> getCurrentAuditor() {
//         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
//         if (authentication == null || !authentication.isAuthenticated()) {
//             return Optional.of("SYSTEM");
//         }
        
//         Object principal = authentication.getPrincipal();
        
//         if (principal instanceof CustomUserPrincipal) {
//             CustomUserPrincipal currentUser = (CustomUserPrincipal) principal;
//             // Pakai userGroupName
//             return Optional.of(currentUser.getUserGroupName());
//         }
        
//         return Optional.of("SYSTEM");
//     }
// }