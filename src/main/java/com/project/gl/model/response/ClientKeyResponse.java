package com.project.gl.model.response;

public class ClientKeyResponse {
    private Long id;
    private String clientName;
    private String clientId;
    private String clientSecret;

    // Constructor
    public ClientKeyResponse() {}

    public ClientKeyResponse(Long id, String clientName, String clientId, String clientSecret) {
        this.id = id;
        this.clientName = clientName;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }
}