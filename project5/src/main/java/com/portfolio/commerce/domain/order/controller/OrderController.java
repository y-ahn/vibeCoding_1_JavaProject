package com.portfolio.commerce.domain.order.controller;

import com.portfolio.commerce.domain.order.dto.*;
import com.portfolio.commerce.domain.order.service.OrderService;
import com.portfolio.commerce.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/orders") @RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private Long userId(UserDetails ud) { return Long.parseLong(ud.getUsername()); }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @AuthenticationPrincipal UserDetails ud,
            @Valid @RequestBody OrderCreateRequest req) {
        return ResponseEntity.ok(ApiResponse.success(
                orderService.createOrder(userId(ud), req)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrders(
            @AuthenticationPrincipal UserDetails ud,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                orderService.getMyOrders(userId(ud), pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @AuthenticationPrincipal UserDetails ud, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                orderService.getOrder(userId(ud), id)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @AuthenticationPrincipal UserDetails ud, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                orderService.cancelOrder(userId(ud), id)));
    }
}
