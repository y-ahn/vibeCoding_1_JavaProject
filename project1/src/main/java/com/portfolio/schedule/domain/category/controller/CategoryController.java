package com.portfolio.schedule.domain.category.controller;

import com.portfolio.schedule.domain.category.dto.CategoryRequest;
import com.portfolio.schedule.domain.category.dto.CategoryResponse;
import com.portfolio.schedule.domain.category.service.CategoryService;
import com.portfolio.schedule.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    private Long userId(UserDetails ud) { return Long.parseLong(ud.getUsername()); }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(
            @AuthenticationPrincipal UserDetails ud,
            @Valid @RequestBody CategoryRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(categoryService.create(userId(ud), req)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> list(
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getMyCategories(userId(ud))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @AuthenticationPrincipal UserDetails ud,
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest req) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.update(userId(ud), id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetails ud,
            @PathVariable Long id) {
        categoryService.delete(userId(ud), id);
        return ResponseEntity.noContent().build();
    }
}
