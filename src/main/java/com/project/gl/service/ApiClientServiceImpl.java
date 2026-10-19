package com.project.gl.service;

import com.project.gl.model.entity.ApiClient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.project.gl.model.response.ApiClientResponse;
import com.project.gl.model.response.ClientKeyResponse;
import com.project.gl.model.response.PageResponse;
// import com.project.gl.model.response.ApiClientFinalResponse;
import com.project.gl.repo.ApiClientRepository;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.security.SecureRandom;
import java.util.Base64;
// import java.security.SecureRandom;
// import java.time.LocalDateTime;
// import java.util.UUID;


@Service
@Transactional
public class ApiClientServiceImpl implements ApiClientService {

    @Autowired
    private ApiClientRepository apiClientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ApiClientResponse> getAllApiClient(
                                                int page, 
                                                int size, 
                                                String sortBy, 
                                                String direction, 
                                                String client_name,
                                                String client_id,
                                                Boolean active                                                          
                                                ) {

        Sort sort = direction.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Panggil repository dengan filter
        Page<ApiClient> pageB = apiClientRepository.findAllWithFilters(
           client_name, client_id, active, pageable
        );
        
        return new PageResponse<>(
            pageB.getContent().stream()
                .map(ApiClientResponse::new)
                .collect(Collectors.toList()),
            pageB.getNumber(),
            pageB.getSize(),
            pageB.getTotalElements(),
            pageB.getTotalPages(),
            pageB.isLast()
        );
    }
   
    // @Transactional
    // public ApiClientFinalResponse createApi(ApiClientFinal request) {

       

    //     // 1. Normalisasi clientId
    //     String cleanedClientId = slugifyClientId(request.getClientId());
    //     //System.out.println("AAAAA="+request.getClientId());
    //     // 2. Validasi clientId harus unik
    //     if (apiClientRepository.existsByClientId(cleanedClientId)) {
    //         throw new RuntimeException("Client ID sudah terpakai: " + cleanedClientId);
    //     }
        
    //     // 2. Generate client secret kalau tidak diisi
    //     String rawSecret = request.getClientId();
    //     if (rawSecret == null || rawSecret.isBlank()) {
    //         rawSecret = UUID.randomUUID().toString().replace("-", "");
    //     }

    //     // 3. Build entity
    //     ApiClientFinal entity = new ApiClientFinal();
    //     entity.setClientName(request.getClientName());
    //     entity.setDescription(request.getDescription());
    //     entity.setClientId(cleanedClientId);
    //     entity.setClientSecretHash(passwordEncoder.encode(rawSecret)); // simpan hash
    //     entity.setActive(request.getActive() != null ? request.getActive() : true);
    //     entity.setExpiredAt(request.getExpiredAt());

    //     // 4. Save
    //     ApiClientFinal saved = apiClientRepository.save(entity);

    //     // 5. Build response (raw secret ditampilkan sekali)
    //     ApiClientFinalResponse response = new ApiClientFinalResponse(saved);
    //     response.setClientSecretHash(entity.getClientSecretHash()); 
        
    //     return response;
    // }

    @Transactional
    @Override
    public ClientKeyResponse createApi(ApiClient request) {

        // 1. Normalisasi clientId
        String cleanedClientId = slugifyClientId(request.getClientId());

        // 2. Validasi clientId harus unik
        if (apiClientRepository.existsByClientId(cleanedClientId)) {
            throw new RuntimeException(
                "Client ID sudah terpakai: " + cleanedClientId
            );
        }

        // 3. Generate client secret BARU
        String rawSecret = generateClientSecret();

        // 4. Build entity
        ApiClient entity = new ApiClient();

        entity.setClientName(request.getClientName());
        entity.setDescription(request.getDescription());
        entity.setClientId(cleanedClientId);

        // Simpan HASH, bukan raw secret
        entity.setClientSecretHash(
            passwordEncoder.encode(rawSecret)
        );

        entity.setActive(
            request.getActive() != null
                ? request.getActive()
                : true
        );

        entity.setExpiredAt(request.getExpiredAt());

        // 5. Save
        ApiClient saved = apiClientRepository.save(entity);

        // 6. Response
        ClientKeyResponse response =  new ClientKeyResponse();
        response.setId(saved.getId());
        response.setClientId(saved.getClientId());
        response.setClientName(saved.getClientName());
        // RAW SECRET hanya dikirim sekali
        response.setClientSecret(rawSecret);

        return response;
    }


    @Transactional
    @Override
    public ClientKeyResponse regenerate(Long id) {

        // 1. Cari API Client
        ApiClient entity = apiClientRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("API Client tidak ditemukan: " + id)
                );

        // 2. Generate RAW secret baru
        String rawSecret = generateClientSecret();

        // 3. Hash secret baru
        String secretHash = passwordEncoder.encode(rawSecret);

        // 4. Ganti secret lama dengan hash baru
        entity.setClientSecretHash(secretHash);

        // 5. Simpan
        ApiClient saved = apiClientRepository.save(entity);

        // 6. Buat response
        ClientKeyResponse response = new ClientKeyResponse();
        
        response.setId(saved.getId());
        response.setClientId(saved.getClientId());
        response.setClientName(saved.getClientName());
        // RAW SECRET hanya dikirim sekali
        response.setClientSecret(rawSecret);


        return response;
    }

    // private String generateClientSecret() {

    //     return UUID.randomUUID()
    //             .toString()
    //             .replace("-", "")
    //             .substring(0, 32);
    // }

    private String generateClientSecret() {

        byte[] randomBytes = new byte[32];

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

   private String slugifyClientId(String input) {
        if (input == null) return null;
        String result = input.trim()
                .toLowerCase(java.util.Locale.ROOT)
                .replaceAll("[^a-z0-9\\s-]", "")  // hapus . , ! @ # dll
                .replaceAll("\\s+", "-")          // spasi jadi -
                .replaceAll("-+", "-")            // -- jadi -
                .replaceAll("^-|-$", "");         // trim - di ujung

        return result.isBlank() ? null : result;
    }

    
    @Override
    public void aktifNonAktif(Long id, String type) {

        ApiClient entity = apiClientRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("API Client tidak ditemukan: " + id)
                );

        boolean newStatus;

        if ("Aktifkan".equalsIgnoreCase(type)) {
            newStatus = true;
        } else if ("Non Aktifkan".equalsIgnoreCase(type)) {
            newStatus = false;
        } else {
            throw new RuntimeException("Type tidak valid: " + type);
        }

        entity.setActive(newStatus);
        apiClientRepository.save(entity);
    }


    @Override
    public ApiClient findById(Long id) {
        return apiClientRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("API Client tidak ditemukan: " + id)
                );
    }


   @Override
    public void update(Long id, ApiClient request) {

        ApiClient entity = apiClientRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("API Client tidak ditemukan: " + id)
                );

        // hanya field ini yang diupdate
        entity.setClientName(request.getClientName());
        entity.setDescription(request.getDescription());
        entity.setActive(request.getActive());
        entity.setExpiredAt(request.getExpiredAt());
        apiClientRepository.save(entity);
    }
    
}
