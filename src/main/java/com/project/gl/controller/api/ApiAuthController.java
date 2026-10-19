package com.project.gl.controller.api;

import com.project.gl.model.request.ApiTokenRequest;
import com.project.gl.model.response.ApiTokenResponse;
import com.project.gl.service.ApiTokenService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class ApiAuthController {

    private final ApiTokenService apiTokenService;

    public ApiAuthController(ApiTokenService apiTokenService) {
        this.apiTokenService = apiTokenService;
    }

    @PostMapping("/token")
    public ResponseEntity<ApiTokenResponse> token(@RequestBody ApiTokenRequest request) {
        ApiTokenResponse response = apiTokenService.generateToken(request);
        return ResponseEntity.ok(response);
    }
}