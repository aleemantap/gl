package com.project.gl.model.response;


import java.time.LocalDateTime;
import com.project.gl.model.entity.UserGroup;

public class GroupResponse {
    private Integer id;
    private String name;
    private String description;
    private String active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public GroupResponse(UserGroup user) {
        this.id = user.getId();
        this.name = user.getName();
        this.description = user.getDescription();
        this.active = user.getActive();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
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

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
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
}

