package com.project.gl.model.response;


import com.project.gl.model.entity.ApiClient;
import java.time.LocalDateTime;

public class ApiClientResponse {
    private Long id;
    private String clientName;
    private String description;
    private String clientId;
    private Boolean active;
    private LocalDateTime expiredAt;
    private LocalDateTime lastUsedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String clientSecretHash;
    private String clientSecret;

    

    public ApiClientResponse(ApiClient apiClient) {
        this.id = apiClient.getId();
        this.clientName = apiClient.getClientName();
        this.description = apiClient.getDescription();
        this.clientId = apiClient.getClientId();
        this.active = apiClient.getActive();
        this.expiredAt = apiClient.getExpiredAt();
        this.lastUsedAt = apiClient.getLastUsedAt();
        this.createdAt = apiClient.getCreatedAt();
        this.updatedAt = apiClient.getUpdatedAt();
        this.clientSecretHash = apiClient.getClientSecretHash();
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

     public String getClientSecretHash() {
        return clientSecretHash;
    }
    public void setClientSecretHash(String clientSecretHash) {
        this.clientSecretHash = clientSecretHash;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Boolean getActive() {
        return active;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }
    public void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }
    public void setLastUsedAt(LocalDateTime lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
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