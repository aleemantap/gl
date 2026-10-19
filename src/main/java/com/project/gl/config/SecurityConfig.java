package com.project.gl.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.project.gl.security.ApiAuthenticationEntryPoint;
import com.project.gl.security.BearerTokenFilter;
import com.project.gl.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BearerTokenFilter bearerTokenFilter;

    @Autowired
    private ApiAuthenticationEntryPoint apiAuthenticationEntryPoint;

    /**
     * ============================================================
     * API SECURITY
     * ============================================================
     *
     * Semua endpoint /api/**
     * menggunakan JWT / Bearer Token
     * dan tidak menggunakan HTTP Session.
     */
    @Bean
    @org.springframework.core.annotation.Order(1)
    public SecurityFilterChain apiSecurityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
            .securityMatcher("/api/**")

            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .exceptionHandling(exception ->
                exception.authenticationEntryPoint(
                    apiAuthenticationEntryPoint
                )
            )

            .authorizeHttpRequests(auth -> auth

                .requestMatchers(
                    "/api/v1/auth/token"
                )
                .permitAll()

                .anyRequest()
                .authenticated()
            )

            .addFilterBefore(
                bearerTokenFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    /**
     * ============================================================
     * WEB SECURITY
     * ============================================================
     *
     * Web menggunakan Form Login + HTTP Session.
     */
    @Bean
    @org.springframework.core.annotation.Order(2)
    public SecurityFilterChain webSecurityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

            // ====================================================
            // WEB TIDAK menggunakan /api/**
            // ====================================================
            .securityMatcher("/**")

            // ====================================================
            // CSRF
            // ====================================================
            .csrf(csrf -> csrf.disable())

            // ====================================================
            // SESSION
            // WEB = SESSION
            // ====================================================
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.IF_REQUIRED
                )
            )

            // ====================================================
            // EXCEPTION HANDLING
            // ====================================================
            .exceptionHandling(exception ->
                exception
                    .accessDeniedPage("/403")
            )

            // ====================================================
            // AUTHORIZATION
            // ====================================================
            .authorizeHttpRequests(auth -> auth

                // Static resources Spring
                .requestMatchers(
                    PathRequest.toStaticResources()
                        .atCommonLocations()
                )
                .permitAll()

                // Login page
                .requestMatchers("/login")
                .permitAll()

                // Public resources
                .requestMatchers(
                    "/img/logo-gadai.png",
                    "/lib/**",
                    "/js/**"
                )
                .permitAll()

                // Semua halaman web lainnya
                .anyRequest()
                .authenticated()
            )

            // ====================================================
            // FORM LOGIN
            // ====================================================
            .formLogin(form -> form

                .loginPage("/login")

                .loginProcessingUrl("/authenticate")

                .defaultSuccessUrl(
                    "/dashboard",
                    true
                )

                .failureUrl(
                    "/login?error=true"
                )

                .failureHandler(
                    (request, response, exception) -> {

                        String errorMessage = "error";

                        if (
                            exception.getMessage() != null
                            && exception.getMessage()
                                .equals("inactive")
                        ) {
                            errorMessage = "inactive";
                        }

                        response.sendRedirect(
                            "/login?error=" + errorMessage
                        );
                    }
                )

                .usernameParameter("email")

                .passwordParameter("password")

                .permitAll()
            )

            // ====================================================
            // LOGOUT
            // ====================================================
            .logout(logout -> logout

                .logoutUrl("/logout")

                .logoutSuccessUrl(
                    "/login?logout"
                )

                .invalidateHttpSession(true)

                .clearAuthentication(true)

                .deleteCookies("JSESSIONID")

                .permitAll()
            );

        return http.build();
    }

    /**
     * ============================================================
     * AUTHENTICATION MANAGER
     * ============================================================
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig
    ) throws Exception {

        return authConfig.getAuthenticationManager();
    }
}