package com.portfolio.commerce.domain.payment.controller;

import com.portfolio.commerce.domain.payment.dto.*;
import com.portfolio.commerce.domain.payment.service.PaymentService;
import com.portfolio.commerce.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private Long userId(UserDetails ud) { return Long.parseLong(ud.getUsername()); }

    // 결제 검증 — 포트원 결제 후 서버 검증
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<PaymentResponse>> verify(
            @AuthenticationPrincipal UserDetails ud,
            @Valid @RequestBody PaymentVerifyRequest req) {
        return ResponseEntity.ok(ApiResponse.success(
                paymentService.verifyAndComplete(userId(ud), req)));
    }

    // 결제 정보 조회
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(
            @AuthenticationPrincipal UserDetails ud,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(
                paymentService.getPayment(userId(ud), orderId)));
    }
}
