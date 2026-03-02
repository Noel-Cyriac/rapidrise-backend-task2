package com.nc.task2.controller;

import com.nc.task2.dto.RegisterRequest;
import com.nc.task2.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> req) {
        return authService.login(req);
    }

    @PostMapping("/refresh")
    public Map<String, String> refresh(@RequestBody Map<String, String> req) {
        return authService.refresh(req.get("refreshToken"));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        return ResponseEntity.ok(authService.logout(request));
    }
}