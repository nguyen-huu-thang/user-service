package vn.xime.user.config.security;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import vn.xime.user.infrastructure.security.filter.JwtAuthenticationFilter;


/**
 * =========================================================
 * SECURITY CONFIGURATION
 * =========================================================
 *
 * User Service Security Boundary.
 *
 * Responsibility:
 *
 * - define public endpoints
 * - define protected endpoints
 * - configure stateless authentication
 * - configure REST API security
 * - integrate JWT authentication filter
 *
 * =========================================================
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter
        jwtAuthenticationFilter;


    @Bean
    SecurityFilterChain securityFilterChain(
        HttpSecurity http
    ) throws Exception {

        http

            // =================================================
            // REST API
            // =================================================

            .csrf(csrf -> csrf.disable())


            // =================================================
            // STATELESS AUTHENTICATION
            // =================================================

            .sessionManagement(session ->

                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )


            // =================================================
            // JWT FILTER
            // =================================================
            //
            // Verify bearer token
            // trước authorization phase.
            //
            // =================================================

            .addFilterBefore(

                jwtAuthenticationFilter,

                UsernamePasswordAuthenticationFilter.class
            )


            // =================================================
            // AUTHORIZATION RULES
            // =================================================

            .authorizeHttpRequests(auth -> auth

                // =============================================
                // PUBLIC APIs
                // =============================================

                .requestMatchers(

                    "/api/v1/public/**"

                ).permitAll()


                // =============================================
                // SWAGGER / OPENAPI
                // =============================================

                .requestMatchers(

                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**"

                ).permitAll()


                // =============================================
                // ACTUATOR
                // =============================================

                .requestMatchers(

                    "/actuator/health"

                ).permitAll()


                // =============================================
                // INTERNAL APIs
                // =============================================

                .requestMatchers(

                    "/internal/**"

                ).authenticated()


                // =============================================
                // MY PROFILE APIs
                // =============================================

                .requestMatchers(

                    "/api/v1/me/**",
                    "/api/v1/profile/me/**"

                ).authenticated()


                // =============================================
                // EVERYTHING ELSE
                // =============================================

                .anyRequest()
                .authenticated()
            );


        return http.build();
    }
}