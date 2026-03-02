package com.portfolio.feed.domain.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingService {

    private final RedisTemplate<String, Object> redisTemplate;

    // Redis Sorted Set 키
    // Sorted Set: score 기준 자동 정렬 — 인기 랭킹에 최적
    private static final String RANKING_KEY = "feed:ranking:posts";

    @Value("${feed.ranking.like-score:2}")
    private double likeScore;

    @Value("${feed.ranking.comment-score:3}")
    private double commentScore;

    @Value("${feed.ranking.top-size:10}")
    private int topSize;

    // 좋아요 점수 추가
    // ZINCRBY feed:ranking:posts +2 "postId"
    public void addLikeScore(Long postId) {
        try {
            redisTemplate.opsForZSet().incrementScore(
                    RANKING_KEY, String.valueOf(postId), likeScore);
        } catch (Exception e) {
            log.warn("좋아요 점수 추가 실패 postId={}: {}", postId, e.getMessage());
        }
    }

    // 좋아요 취소 점수 차감
    public void removeLikeScore(Long postId) {
        try {
            Double current = redisTemplate.opsForZSet().score(
                    RANKING_KEY, String.valueOf(postId));
            if (current != null && current >= likeScore) {
                redisTemplate.opsForZSet().incrementScore(
                        RANKING_KEY, String.valueOf(postId), -likeScore);
            }
        } catch (Exception e) {
            log.warn("좋아요 점수 차감 실패 postId={}: {}", postId, e.getMessage());
        }
    }

    // 댓글 점수 추가
    public void addCommentScore(Long postId) {
        try {
            redisTemplate.opsForZSet().incrementScore(
                    RANKING_KEY, String.valueOf(postId), commentScore);
        } catch (Exception e) {
            log.warn("댓글 점수 추가 실패 postId={}: {}", postId, e.getMessage());
        }
    }

    // TOP N 인기 게시글 ID 목록 (score 내림차순)
    // ZREVRANGE feed:ranking:posts 0 9
    public List<Long> getTopPostIds() {
        try {
            Set<Object> result = redisTemplate.opsForZSet()
                    .reverseRange(RANKING_KEY, 0, topSize - 1);
            if (result == null) return Collections.emptyList();
            return result.stream()
                    .map(id -> Long.parseLong(id.toString()))
                    .toList();
        } catch (Exception e) {
            log.warn("랭킹 조회 실패: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    // 특정 게시글 점수 조회
    public Double getScore(Long postId) {
        try {
            return redisTemplate.opsForZSet()
                    .score(RANKING_KEY, String.valueOf(postId));
        } catch (Exception e) {
            return 0.0;
        }
    }
}
