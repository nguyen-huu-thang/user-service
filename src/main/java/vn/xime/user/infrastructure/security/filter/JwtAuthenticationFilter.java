package vn.xime.user.infrastructure.security.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import vn.xime.user.application.service.authentication.VerifyAccessToken;


/**
 * =========================================================
 * JWT AUTHENTICATION FILTER
 * =========================================================
 *
 * Responsibility:
 *
 * - extract bearer token
 * - verify JWT access token
 * - extract identity id
 * - create Authentication object
 * - set SecurityContext
 *
 * Notes:
 *
 * - stateless authentication
 * - no HTTP session
 * - executed once per request
 *
 * =========================================================
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
    extends OncePerRequestFilter {

    private final VerifyAccessToken verifyAccessToken;


    @Override
    protected void doFilterInternal(

        HttpServletRequest request,

        HttpServletResponse response,

        FilterChain filterChain

    ) throws ServletException, IOException {

        /*
         * =========================
         * AUTHORIZATION HEADER
         * =========================
         */

        String authorization =
            request.getHeader(
                "Authorization"
            );


        /*
         * =========================
         * NO TOKEN
         * =========================
         *
         * Không có bearer token
         * -> bỏ qua
         *
         * Spring Security sẽ xử lý
         * authorization rule phía sau.
         *
         * =========================
         */

        if (
            authorization == null ||
            !authorization.startsWith("Bearer ")
        ) {

            filterChain.doFilter(
                request,
                response
            );

            return;
        }


        /*
         * =========================
         * EXTRACT TOKEN
         * =========================
         */

        String accessToken = authorization.substring(7);


        try {

            /*
             * =========================
             * VERIFY JWT
             * =========================
             */

            String identityId =
                verifyAccessToken.execute(
                    accessToken
                );


            /*
             * =========================
             * CREATE AUTHENTICATION
             * =========================
             */

            Authentication authentication =

                new UsernamePasswordAuthenticationToken(

                    identityId,

                    null,

                    null
                );


            /*
             * =========================
             * SET SECURITY CONTEXT
             * =========================
             */

            SecurityContextHolder
                .getContext()
                .setAuthentication(
                    authentication
                );

        } catch (Exception ex) {

            /*
             * =========================
             * INVALID TOKEN
             * =========================
             *
             * Clear context để tránh
             * auth state rác.
             *
             * =========================
             */
            SecurityContextHolder.clearContext();
        }


        /*
         * =========================
         * CONTINUE FILTER CHAIN
         * =========================
         */

        filterChain.doFilter(
            request,
            response
        );
    }
}