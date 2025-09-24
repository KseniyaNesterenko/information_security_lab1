package org.example.information_security_lab1.controller;

import org.example.information_security_lab1.security.JwtUtil;
import org.example.information_security_lab1.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil  jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody Map<String, String> payload) {
        authService.register(payload.get("username"), payload.get("password"));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> payload) {
        return authService.authenticate(payload.get("username"), payload.get("password"))
                .map(user -> Map.of(
                        "accessToken", jwtUtil.generateAccessToken(user.getUsername()),
                        "refreshToken", jwtUtil.generateRefreshToken(user.getUsername())
                ))
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
    }

    @PostMapping("/refresh")
    public Map<String, String> refresh(@RequestBody Map<String, String> payload) {
        String refreshToken = payload.get("refreshToken");
        String username = jwtUtil.validateAndGetUsername(refreshToken);

        return Map.of(
                "accessToken", jwtUtil.generateAccessToken(username),
                "refreshToken", jwtUtil.generateRefreshToken(username)
        );
    }
}
