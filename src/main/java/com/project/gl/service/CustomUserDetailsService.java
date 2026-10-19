package com.project.gl.service;

import com.project.gl.config.CustomUserPrincipal;
import com.project.gl.model.entity.User;
import com.project.gl.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    // @Autowired
    // private PasswordEncoder passwordEncoder;

    // @Override
    // public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    //     User user = userRepository.findByEmail(email)
    //         .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
    //     // ========== TAMBAHKAN VALIDASI INI ==========
    //     // Cek apakah user aktif
    //     if (user.getActive() == null || !user.getActive().equalsIgnoreCase("Y")) {
    //         throw new UsernameNotFoundException("User is inactive or disabled: " + email);
    //     }
    //     // ===========================================
        
    //     return new CustomUserPrincipal(user);
    // }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // pakai query yang JOIN FETCH relasi
        User user = userRepository.findByEmailWithRelations(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (user.getActive() == null || !user.getActive().equalsIgnoreCase("Y")) {
            throw new UsernameNotFoundException("User is inactive or disabled: " + email);
        }

        return new CustomUserPrincipal(user);
    }
}