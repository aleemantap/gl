package com.project.gl.model.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users") // nama tabel di database Laravel
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
  
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_group_id", insertable = false, updatable = false)
    private UserGroup userGroup;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = true)
    private String avatar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cabang_id", insertable = false, updatable = false)
    private Cabang cabang;

  
    @Column(name="cabang_id", nullable = true)
    private Integer cabangId;

    @Column(name="user_group_id", nullable = true)
    private Integer userGroupId;
  
    @Column(nullable = false)
    private String active = "N";

    @Column(nullable = false)
    private String password; 
    
    @Column(name = "remember_token")
    private String rememberToken;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

     
    public Integer getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(Integer userGroupId) {
        this.userGroupId = userGroupId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

     public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getActive() {
        return active;
    }



    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRememberToken() {
        return rememberToken;
    }

    
    public Integer getCabangId() {
        return cabangId;
    }

    public void setCabangId(Integer cabangId) {
        this.cabangId = cabangId;
    }

    public void setRememberToken(String rememberToken) {
        this.rememberToken = rememberToken;
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

    public void setUserGroup(UserGroup userGroup) {
         this.userGroup = userGroup;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public void setCabang(Cabang cabang) {
        this.cabang = cabang;
    }

    public Cabang getCabang() {
        return cabang;
    }
    
}