package com.qualitymanagementsystem.auth_service.filter;

import com.qualitymanagementsystem.auth_service.repository.BlacklistedTokenRepository;
import com.qualitymanagementsystem.auth_service.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, BlacklistedTokenRepository blacklistedTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Authorization: Bearer <token>
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            // Blacklist check
            if (blacklistedTokenRepository.findByToken(jwt).isPresent()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"please login\"}");
                return;
            }
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                logger.error("Invalid JWT Token", e);
            }
        }


        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(jwt)) {

                String role = jwtUtil.extractRole(jwt);

                User userDetails = new User(
                        username,
                        "",
                        Collections.singleton(() -> "ROLE_" + role)
                );

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // ضيف الـ user للـ Security Context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
