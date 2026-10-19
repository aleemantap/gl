package com.project.gl.service;

import com.project.gl.exception.InvalidApiClientException;
import com.project.gl.model.entity.ApiClient;
import com.project.gl.model.request.ApiTokenRequest;
import com.project.gl.model.response.ApiTokenResponse;
import com.project.gl.repo.ApiClientRepository;
// import com.project.gl.repo.ApiClientRepository;
import com.project.gl.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ApiTokenService {

    private final ApiClientRepository apiClientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public ApiTokenService(
            ApiClientRepository apiClientRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.apiClientRepository = apiClientRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public ApiTokenResponse generateToken(ApiTokenRequest request) {

        // 1. Validasi request
        if (request.getClientId() == null ||
                request.getClientId().isBlank()) {

            throw new RuntimeException("Client ID wajib diisi");
        }

        if (request.getClientSecret() == null ||
                request.getClientSecret().isBlank()) {

            throw new RuntimeException("Client Secret wajib diisi");
        }

        // 2. Cari client berdasarkan client_id
        ApiClient client = apiClientRepository.findByClientId(request.getClientId())
                        .orElseThrow(() ->
                                new InvalidApiClientException(
                                        "Client ID atau Client Secret tidak valid"
                                )
                        );

        // 3. Cek active
        if (!Boolean.TRUE.equals(client.getActive())) {

            throw new RuntimeException(
                    "API Client tidak aktif"
            );
        }

        // 4. Cek expired_at
        if (client.getExpiredAt() != null &&
                client.getExpiredAt().isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "API Client sudah expired"
            );
        }

        // 5. Verifikasi client secret
        boolean validSecret =
                passwordEncoder.matches(
                        request.getClientSecret(),
                        client.getClientSecretHash()
                );

        if (!validSecret) {

            throw  new InvalidApiClientException(
                    "Client ID atau Client Secret tidak valid"
            );
        }

        // 6. Generate JWT
        String accessToken =
                jwtService.generateToken(
                        client.getClientId()
                );

        // 7. Return token
        return new ApiTokenResponse(
                accessToken,
                "Bearer",
                jwtService.getExpiration()
        );
    }
}