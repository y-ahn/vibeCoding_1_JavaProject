package com.portfolio.feed.domain.feed.controller;

import com.portfolio.feed.domain.feed.dto.RankingResponse;
import com.portfolio.feed.domain.feed.service.FollowService;
import com.portfolio.feed.domain.post.service.RankingService;
import com.portfolio.feed.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController @RequestMapping("/api") @RequiredArgsConstructor
public class FeedController {

    private final FollowService followService;
    private final RankingService rankingService;

    private Long userId(UserDetails ud) { return Long.parseLong(ud.getUsername()); }

    // 팔로우
    @PostMapping("/users/{targetId}/follow")
    public ResponseEntity<ApiResponse<Void>> follow(
            @AuthenticationPrincipal UserDetails ud, @PathVariable Long targetId) {
        followService.follow(userId(ud), targetId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 언팔로우
    @DeleteMapping("/users/{targetId}/follow")
    public ResponseEntity<ApiResponse<Void>> unfollow(
            @AuthenticationPrincipal UserDetails ud, @PathVariable Long targetId) {
        followService.unfollow(userId(ud), targetId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 인기 게시글 TOP N 랭킹
    @GetMapping("/posts/ranking")
    public ResponseEntity<ApiResponse<List<RankingResponse>>> getRanking() {
        AtomicInteger rank = new AtomicInteger(1);
        List<RankingResponse> ranking = rankingService.getTopPostIds().stream()
                .map(postId -> new RankingResponse(
                        postId,
                        rankingService.getScore(postId),
                        rank.getAndIncrement()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(ranking));
    }
}
