package com.qualitymanagementsystem.auth_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private String generateToken(String username, String role, long expiration) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public String generateAccessToken(String username, String role){
        return generateToken(username, role, accessTokenExpiration);
    }
    public String generateRefreshToken(String username, String role) {
       return generateToken(username, role, refreshTokenExpiration);
    }
    public String extractUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }
    public boolean validateToken(String token){
        return !isTokenExpired(token);
    }
    public Date getExpirationDate(String token){
        return extractClaim(token, Claims::getExpiration);
    }
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Boolean isTokenExpired(String token){
       Date expiration = getExpirationDate(token);
       return expiration.before(new Date());
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    public boolean isRefreshTokenValid(String token, org.springframework.security.core.userdetails.UserDetails user) {
        return !isTokenExpired(token) && extractUsername(token).equals(user.getUsername());
    }
}
