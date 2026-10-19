package com.project.gl.helper;

import com.project.gl.config.CustomUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
public class SecurityUtil {
    
    public static CustomUserPrincipal getCurrentUser() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null ||
            !(auth.getPrincipal() instanceof CustomUserPrincipal)) {
            return null;
        }

        return (CustomUserPrincipal) auth.getPrincipal();
    }

    
}
