package com.project.gl.security;

//yang di komen CADANGAN multi key

// import java.io.IOException;
// import java.util.List;

// import org.springframework.http.MediaType;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.project.gl.security.JwtService;

// import io.jsonwebtoken.Claims;
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// @Component
// public class BearerTokenFilter extends OncePerRequestFilter {

//     private final JwtService jwtService;
//     private final ObjectMapper objectMapper;

//     public BearerTokenFilter(
//             JwtService jwtService,
//             ObjectMapper objectMapper) {

//         this.jwtService = jwtService;
//         this.objectMapper = objectMapper;
//     }

//     @Override
//     protected boolean shouldNotFilter(
//             HttpServletRequest request) {

//         return request.getServletPath().equals("/api/v1/auth/token");
//     }

//     @Override
//     protected void doFilterInternal(
//             HttpServletRequest request,
//             HttpServletResponse response,
//             FilterChain filterChain)
//             throws ServletException, IOException {

//         String authorizationHeader = request.getHeader("Authorization");

//         // Tidak ada Bearer Token
//         if (authorizationHeader == null
//                 || !authorizationHeader.startsWith("Bearer ")) {

//             filterChain.doFilter(request, response);
//             return;
//         }

//         String token = authorizationHeader.substring(7);

//         try {

//             // =====================================================
//             // VALIDASI TOKEN
//             // =====================================================

//             if (!jwtService.isTokenValid(token)) {

//                 sendUnauthorized(
//                     response,
//                     "Access token tidak valid atau sudah expired"
//                 );

//                 return;
//             }

//             // =====================================================
//             // AMBIL CLAIM
//             // =====================================================

//             Claims claims =
//                     jwtService.extractClaims(token);

//             String clientId =
//                     claims.getSubject();

//             // =====================================================
//             // BUAT AUTHENTICATION
//             // =====================================================

//             UsernamePasswordAuthenticationToken authentication =
//                     new UsernamePasswordAuthenticationToken(
//                             clientId,
//                             null,
//                             List.of(
//                                 new SimpleGrantedAuthority(
//                                     "ROLE_API_CLIENT"
//                                 )
//                             )
//                     );

//             SecurityContextHolder
//                     .getContext()
//                     .setAuthentication(authentication);

//             // Lanjut ke controller
//             filterChain.doFilter(request, response);

//         } catch (Exception e) {

//             SecurityContextHolder.clearContext();

//             sendUnauthorized(
//                 response,
//                 "Access token tidak valid atau sudah expired"
//             );
//         }
//     }

//     private void sendUnauthorized(
//             HttpServletResponse response,
//             String message)
//             throws IOException {

//         response.setStatus(
//             HttpServletResponse.SC_UNAUTHORIZED
//         );

//         response.setContentType(
//             MediaType.APPLICATION_JSON_VALUE
//         );

//         response.setCharacterEncoding("UTF-8");

//         ApiErrorResponse error =
//                 new ApiErrorResponse(
//                     false,
//                     message
//                 );

//         objectMapper.writeValue(
//             response.getWriter(),
//             error
//         );
//     }

//     public record ApiErrorResponse(
//             boolean success,
//             String message
//     ) {}
// }


// cadangan auth tanpa client_id
import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.gl.model.entity.ApiClient;
import com.project.gl.repo.ApiClientRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class BearerTokenFilter extends OncePerRequestFilter {

    private final ApiClientRepository apiClientRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    public BearerTokenFilter(
            ApiClientRepository apiClientRepository,
            PasswordEncoder passwordEncoder,
            ObjectMapper objectMapper) {

        this.apiClientRepository = apiClientRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        return request.getServletPath()
                .equals("/api/v1/auth/token");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        // Tidak ada Bearer Token
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = authorizationHeader.substring(7);

        try {

            // =====================================================
            // AMBIL API CLIENT
            // =====================================================

            ApiClient client =  apiClientRepository.findFirst()
                            .orElse(null);

            if (client == null) {

                sendUnauthorized(
                        response,
                        "API Client belum tersedia"
                );

                return;
            }

            // =====================================================
            // VALIDASI API KEY
            // =====================================================

            boolean validApiKey =
                    passwordEncoder.matches(
                            apiKey,
                            client.getClientSecretHash()
                    );

            if (!validApiKey) {

                sendUnauthorized(
                        response,
                        "API Key tidak valid"
                );

                return;
            }

            // =====================================================
            // CEK ACTIVE
            // =====================================================

            if (!Boolean.TRUE.equals(client.getActive())) {

                sendUnauthorized(
                        response,
                        "API Client tidak aktif"
                );

                return;
            }
            // =====================================================
            // BUAT AUTHENTICATION
            // =====================================================
            String clientId = client.getClientId();
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            clientId,
                            null,
                            List.of(
                                    new SimpleGrantedAuthority(
                                            "ROLE_API_CLIENT"
                                    )
                            )
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Lanjut ke controller
            filterChain.doFilter(
                    request,
                    response
            );

        } catch (Exception e) {

            SecurityContextHolder.clearContext();

            sendUnauthorized(
                    response,
                    "API Key tidak valid"
            );
        }
    }

    private void sendUnauthorized(
            HttpServletResponse response,
            String message)
            throws IOException {

        response.setStatus(
                HttpServletResponse.SC_UNAUTHORIZED
        );

        response.setContentType(
                MediaType.APPLICATION_JSON_VALUE
        );

        response.setCharacterEncoding("UTF-8");

        ApiErrorResponse error =
                new ApiErrorResponse(
                        false,
                        message
                );

        objectMapper.writeValue(
                response.getWriter(),
                error
        );
    }

    public record ApiErrorResponse(
            boolean success,
            String message
    ) {}
}

/* auth dengan client_id */
// import java.io.IOException;
// import java.util.List;

// import org.springframework.http.MediaType;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.project.gl.model.entity.ApiClient;
// import com.project.gl.repo.ApiClientRepository;

// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// @Component
// public class BearerTokenFilter extends OncePerRequestFilter {

//     private final ApiClientRepository apiClientRepository;
//     private final PasswordEncoder passwordEncoder;
//     private final ObjectMapper objectMapper;

//     public BearerTokenFilter(
//             ApiClientRepository apiClientRepository,
//             PasswordEncoder passwordEncoder,
//             ObjectMapper objectMapper) {

//         this.apiClientRepository = apiClientRepository;
//         this.passwordEncoder = passwordEncoder;
//         this.objectMapper = objectMapper;
//     }

//     @Override
//     protected boolean shouldNotFilter(HttpServletRequest request) {

//         return request.getServletPath()
//                 .equals("/api/v1/auth/token");
//     }

//     @Override
//     protected void doFilterInternal(
//             HttpServletRequest request,
//             HttpServletResponse response,
//             FilterChain filterChain)
//             throws ServletException, IOException {

//         String authorizationHeader =  request.getHeader("Authorization");

//         // Tidak ada Bearer Token
//         if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
//             filterChain.doFilter(request, response);
//             return;
//         }

//         String apiKey =  authorizationHeader.substring(7);

//         // =====================================================
//         // AMBIL CLIENT ID
//         // =====================================================

//         String clientId =  request.getHeader("X-Client-Id");

//         if (clientId == null || clientId.isBlank()) {

//             sendUnauthorized(
//                     response,
//                     "Client ID wajib diisi"
//             );

//             return;
//         }

//         try {

//             // =====================================================
//             // CARI API CLIENT BERDASARKAN CLIENT ID
//             // =====================================================

//             ApiClient client =  apiClientRepository
//                             .findByClientId(clientId)
//                             .orElse(null);

//             if (client == null) {

//                 sendUnauthorized(
//                         response,
//                         "Client ID tidak valid"
//                 );

//                 return;
//             }

//             // =====================================================
//             // VALIDASI API KEY
//             // =====================================================

//             boolean validApiKey = passwordEncoder.matches(
//                             apiKey,
//                             client.getClientSecretHash()
//                     );

//             if (!validApiKey) {

//                 sendUnauthorized(
//                         response,
//                         "API Key tidak valid"
//                 );

//                 return;
//             }

//             // =====================================================
//             // CEK ACTIVE
//             // =====================================================

//             if (!Boolean.TRUE.equals(client.getActive())) {

//                 sendUnauthorized(
//                         response,
//                         "API Client tidak aktif"
//                 );

//                 return;
//             }

//             // =====================================================
//             // BUAT AUTHENTICATION
//             // =====================================================

//             UsernamePasswordAuthenticationToken authentication =
//                     new UsernamePasswordAuthenticationToken(
//                             client.getClientId(),
//                             null,
//                             List.of(
//                                     new SimpleGrantedAuthority(
//                                             "ROLE_API_CLIENT"
//                                     )
//                             )
//                     );

//             SecurityContextHolder.getContext().setAuthentication(authentication);

//             // =====================================================
//             // LANJUT KE CONTROLLER
//             // =====================================================

//             filterChain.doFilter(
//                     request,
//                     response
//             );

//         } catch (Exception e) {

//             SecurityContextHolder.clearContext();

//             sendUnauthorized(
//                     response,
//                     "API Key tidak valid"
//             );
//         }
//     }

//     private void sendUnauthorized(
//             HttpServletResponse response,
//             String message)
//             throws IOException {

//         response.setStatus(
//                 HttpServletResponse.SC_UNAUTHORIZED
//         );

//         response.setContentType(
//                 MediaType.APPLICATION_JSON_VALUE
//         );

//         response.setCharacterEncoding("UTF-8");

//         ApiErrorResponse error =
//                 new ApiErrorResponse(
//                         false,
//                         message
//                 );

//         objectMapper.writeValue(
//                 response.getWriter(),
//                 error
//         );
//     }

//     public record ApiErrorResponse(
//             boolean success,
//             String message
//     ) {}
// }