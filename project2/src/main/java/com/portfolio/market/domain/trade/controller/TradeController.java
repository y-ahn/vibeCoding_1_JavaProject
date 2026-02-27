package com.portfolio.market.domain.trade.controller;

import com.portfolio.market.domain.trade.dto.TradeResponse;
import com.portfolio.market.domain.trade.service.TradeService;
import com.portfolio.market.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    private Long userId(UserDetails ud) { return Long.parseLong(ud.getUsername()); }

    // 구매 요청
    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse<TradeResponse>> requestTrade(
            @AuthenticationPrincipal UserDetails ud,
            @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(
                tradeService.requestTrade(userId(ud), productId)));
    }

    // 거래 완료 확정 (판매자)
    @PatchMapping("/{tradeId}/complete")
    public ResponseEntity<ApiResponse<TradeResponse>> complete(
            @AuthenticationPrincipal UserDetails ud,
            @PathVariable Long tradeId) {
        return ResponseEntity.ok(ApiResponse.success(
                tradeService.completeTrade(userId(ud), tradeId)));
    }

    // 거래 취소
    @PatchMapping("/{tradeId}/cancel")
    public ResponseEntity<ApiResponse<TradeResponse>> cancel(
            @AuthenticationPrincipal UserDetails ud,
            @PathVariable Long tradeId) {
        return ResponseEntity.ok(ApiResponse.success(
                tradeService.cancelTrade(userId(ud), tradeId)));
    }

    // 구매 내역 조회
    @GetMapping("/purchases")
    public ResponseEntity<ApiResponse<Page<TradeResponse>>> getPurchases(
            @AuthenticationPrincipal UserDetails ud,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                tradeService.getMyPurchases(userId(ud), pageable)));
    }

    // 판매 내역 조회
    @GetMapping("/sales")
    public ResponseEntity<ApiResponse<Page<TradeResponse>>> getSales(
            @AuthenticationPrincipal UserDetails ud,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                tradeService.getMySales(userId(ud), pageable)));
    }
}
