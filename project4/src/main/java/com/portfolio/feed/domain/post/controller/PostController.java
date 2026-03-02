package com.portfolio.feed.domain.post.controller;

import com.portfolio.feed.domain.post.dto.*;
import com.portfolio.feed.domain.post.service.LikeService;
import com.portfolio.feed.domain.post.service.PostService;
import com.portfolio.feed.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final LikeService likeService;

    private Long userId(UserDetails ud) { return Long.parseLong(ud.getUsername()); }

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> create(
            @AuthenticationPrincipal UserDetails ud,
            @Valid @RequestBody PostCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(postService.create(userId(ud), req)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(postService.getOne(id)));
    }

    // 팔로우 피드 (Cursor 페이징)
    @GetMapping("/feed")
    public ResponseEntity<ApiResponse<CursorResult<PostResponse>>> getFeed(
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam(required = false) Long cursor) {
        return ResponseEntity.ok(ApiResponse.success(
                postService.getFeed(userId(ud), cursor)));
    }

    // 내 게시글 (Cursor 페이징)
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<CursorResult<PostResponse>>> getMyPosts(
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam(required = false) Long cursor) {
        return ResponseEntity.ok(ApiResponse.success(
                postService.getMyPosts(userId(ud), cursor)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> update(
            @AuthenticationPrincipal UserDetails ud,
            @PathVariable Long id, @Valid @RequestBody PostCreateRequest req) {
        return ResponseEntity.ok(ApiResponse.success(
                postService.update(userId(ud), id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetails ud, @PathVariable Long id) {
        postService.delete(userId(ud), id);
        return ResponseEntity.noContent().build();
    }

    // 좋아요
    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Integer>> like(
            @AuthenticationPrincipal UserDetails ud, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(likeService.like(userId(ud), id)));
    }

    // 좋아요 취소
    @DeleteMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Integer>> unlike(
            @AuthenticationPrincipal UserDetails ud, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(likeService.unlike(userId(ud), id)));
    }
}
