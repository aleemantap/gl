package com.project.gl.model.response;

import java.time.LocalDateTime;
import com.project.gl.model.entity.User;
import lombok.Builder;




public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Integer userGroupId;
    private String userGroup;
    private Integer cabangId;  
    private String cabangName;
    private String active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.active = user.getActive();
        this.cabangName = user.getCabang().getNamaCabang();
      
        if (user.getUserGroup() != null) {
            this.userGroup = user.getUserGroup().getName();
            this.userGroupId = user.getUserGroup().getId(); 
        }
        
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getActive() {
        return active;
    }
    public void setActive(String active) {
        this.active = active;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Integer getUserGroupId() {
        return userGroupId;
    }
    public void setUserGroupId(Integer userGroupId) {
        this.userGroupId = userGroupId;
    }
    public String getUserGroup() {
        return userGroup;
    }
    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }
  
    public String getCabangName() {
        return cabangName;
    }
    public void setCabangName(String cabangName) {
        this.cabangName = cabangName;
    }
   

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getCabangId() {
        return cabangId;
    }
    public void setCabangId(Integer cabangId) {
        this.cabangId = cabangId;
    }
}

