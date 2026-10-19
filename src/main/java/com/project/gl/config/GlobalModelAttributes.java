package com.project.gl.config;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("currentUser")
    public CustomUserPrincipal currentUser(@AuthenticationPrincipal CustomUserPrincipal currentUser) {

        return currentUser;
    }
}