package com.portfolio.market.domain.product.controller;

import com.portfolio.market.domain.product.dto.*;
import com.portfolio.market.domain.product.entity.ProductStatus;
import com.portfolio.market.domain.product.service.ProductService;
import com.portfolio.market.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    private Long userId(UserDetails ud) { return Long.parseLong(ud.getUsername()); }

    // 상품 등록
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @AuthenticationPrincipal UserDetails ud,
            @Valid @RequestBody ProductCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(productService.create(userId(ud), req)));
    }

    // 상품 검색 (동적 쿼리 — QueryDSL)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> search(
            ProductSearchCondition condition,
            @PageableDefault(size = 10, sort = "createdAt",
                             direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                productService.search(condition, pageable)));
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getOne(id)));
    }

    // 내 상품 목록
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getMyProducts(
            @AuthenticationPrincipal UserDetails ud,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                productService.getMyProducts(userId(ud), pageable)));
    }

    // 상품 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @AuthenticationPrincipal UserDetails ud,
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest req) {
        return ResponseEntity.ok(ApiResponse.success(
                productService.update(userId(ud), id, req)));
    }

    // 상품 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetails ud, @PathVariable Long id) {
        productService.delete(userId(ud), id);
        return ResponseEntity.noContent().build();
    }

    // 거래 상태 변경 (판매중/예약중/판매완료)
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ProductResponse>> changeStatus(
            @AuthenticationPrincipal UserDetails ud,
            @PathVariable Long id,
            @RequestParam ProductStatus status) {
        return ResponseEntity.ok(ApiResponse.success(
                productService.changeStatus(userId(ud), id, status)));
    }
}
