package com.portfolio.market.domain.user.controller;

import com.portfolio.market.domain.user.dto.LoginRequest;
import com.portfolio.market.domain.user.dto.SignupRequest;
import com.portfolio.market.domain.user.dto.TokenResponse;
import com.portfolio.market.domain.user.service.AuthService;
import com.portfolio.market.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<TokenResponse>> signup(
            @Valid @RequestBody SignupRequest req) {
        return ResponseEntity.ok(ApiResponse.success(authService.signup(req)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
            @Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(req)));
    }
}
