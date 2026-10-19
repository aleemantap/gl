package com.project.gl.config;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

import com.project.gl.model.entity.User;

@Getter
public class CustomUserPrincipal implements UserDetails {

    private Long id;
    private String email;
    private String name;
    private Integer userGroupId;
    private String userGroupName;
    private Integer cabangId;
    private String cabangName;
    private String password;
    private String active; 
    
     public CustomUserPrincipal(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.active = user.getActive(); 
        
        if (user.getUserGroup() != null) {
            this.userGroupId = user.getUserGroup().getId();
            this.userGroupName = user.getUserGroup().getName();
        }

        if (user.getCabang() != null) {
            this.cabangId = user.getCabang().getId();
            this.cabangName = user.getCabang().getNamaCabang();
        }
        this.password = user.getPassword();
    }
    
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    // @Override
    // public Collection<? extends GrantedAuthority> getAuthorities() {
    //     return Collections.emptyList();
    // }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        String role = switch (userGroupId) {
            case 1 -> "ROLE_ADMIN";
            case 2 -> "ROLE_MANAGER";
            case 3 -> "ROLE_ACUN";
            default -> "ROLE_USER";
        };

        return List.of(new SimpleGrantedAuthority(role));
    }

    

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // return true;
         return active != null && (active.equalsIgnoreCase("Y"));
    }

   
}