package com.qualitymanagementsystem.auth_service.controller;

import com.qualitymanagementsystem.auth_service.dto.LoginRequestDTO;
import com.qualitymanagementsystem.auth_service.dto.Token;
import com.qualitymanagementsystem.auth_service.service.AuthenticationService;
import com.qualitymanagementsystem.auth_service.service.CustomUserDetailsService;
import com.qualitymanagementsystem.auth_service.util.InMemoryTokenStore;
import com.qualitymanagementsystem.auth_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private com.qualitymanagementsystem.auth_service.repository.BlacklistedTokenRepository blacklistedTokenRepository;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<Token> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        Token token = authenticationService.login(
                loginRequestDTO.username(),
                loginRequestDTO.password()
        );

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // store access token in memory
        InMemoryTokenStore.storeToken(token.accessToken());

        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorization,
                                    @RequestBody(required = false) Map<String, String> body) {
        String accessToken = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            accessToken = authorization.substring(7);
        }
        String refreshToken = body != null ? body.get("refresh_token") : null;
        if (accessToken != null) {
            com.qualitymanagementsystem.auth_service.model.BlacklistedToken blacklistedAccess = new com.qualitymanagementsystem.auth_service.model.BlacklistedToken();
            blacklistedAccess.setToken(accessToken);
            blacklistedAccess.setExpirationDate(jwtUtil.getExpirationDate(accessToken));
            blacklistedTokenRepository.save(blacklistedAccess);
        }
        if (refreshToken != null) {
            com.qualitymanagementsystem.auth_service.model.BlacklistedToken blacklistedRefresh = new com.qualitymanagementsystem.auth_service.model.BlacklistedToken();
            blacklistedRefresh.setToken(refreshToken);
            blacklistedRefresh.setExpirationDate(jwtUtil.getExpirationDate(refreshToken));
            blacklistedTokenRepository.save(blacklistedRefresh);
        }
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    // Refresh token endpoint
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refresh_token");
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body("Missing refresh_token");
        }

        String username;
        try {
            username = jwtUtil.extractUsername(refreshToken);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }

        UserDetails user;
        try {
            user = userDetailsService.loadUserByUsername(username);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("User not found");
        }

        if (!jwtUtil.isRefreshTokenValid(refreshToken, user)) {
            return ResponseEntity.status(401).body("Invalid or expired refresh token");
        }

        String role = jwtUtil.extractRole(refreshToken);
        try {
            String newAccessToken = jwtUtil.generateAccessToken(username, role);
            String newRefreshToken = jwtUtil.generateRefreshToken(username, role);

            // store new access token
            InMemoryTokenStore.storeToken(newAccessToken);

            return ResponseEntity.ok(Map.of(
                    "access_token", newAccessToken,
                    "refresh_token", newRefreshToken
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Token generation error");
        }
    }
}
