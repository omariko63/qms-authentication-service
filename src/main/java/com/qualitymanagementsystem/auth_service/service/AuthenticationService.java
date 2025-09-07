package com.qualitymanagementsystem.auth_service.service;


import com.qualitymanagementsystem.auth_service.dto.Token;
import common.dto.UserDto;
import com.qualitymanagementsystem.auth_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import common.model.User;
import common.repository.UserRepository;
import common.mapper.UserMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    String validationUrl = "http://localhost:8080/api/users/validate";
   /* public AuthenticationService(RestTemplate restTemplate, JwtUtil jwtUtil, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.restTemplate = restTemplate;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }*/


    public Token login(String username, String password) {
        //List<Token> tokens = new ArrayList<>();
        //restTemplate.postForObject()
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Invalid username or password"));
        System.out.println("[UserService] validateUser found user: " + user.getUsername() + ", role: " + (user.getRole() != null ? user.getRole().getRoleName() : "null"));
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw(new IllegalArgumentException("Invalid username or password"));
        }
        UserDto userdto= userMapper.toDto(user);
        String roleName = user.getRole() != null ? user.getRole().getRoleName() : "";
        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), roleName);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), roleName);

        return new Token(accessToken, refreshToken);

    }
}
