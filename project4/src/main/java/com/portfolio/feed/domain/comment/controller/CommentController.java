package com.portfolio.feed.domain.comment.controller;

import com.portfolio.feed.domain.comment.dto.*;
import com.portfolio.feed.domain.comment.service.CommentService;
import com.portfolio.feed.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api") @RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private Long userId(UserDetails ud) { return Long.parseLong(ud.getUsername()); }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> create(
            @AuthenticationPrincipal UserDetails ud,
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest req) {
        return ResponseEntity.ok(ApiResponse.success(
                commentService.create(userId(ud), postId, req)));
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(
            @PathVariable Long postId) {
        return ResponseEntity.ok(ApiResponse.success(commentService.getComments(postId)));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetails ud, @PathVariable Long commentId) {
        commentService.delete(userId(ud), commentId);
        return ResponseEntity.noContent().build();
    }
}
