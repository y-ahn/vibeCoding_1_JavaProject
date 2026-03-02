package com.portfolio.feed.domain.post.service;

import com.portfolio.feed.domain.post.entity.Post;
import com.portfolio.feed.domain.post.entity.PostLike;
import com.portfolio.feed.domain.post.repository.PostLikeRepository;
import com.portfolio.feed.domain.post.repository.PostRepository;
import com.portfolio.feed.domain.user.entity.User;
import com.portfolio.feed.domain.user.repository.UserRepository;
import com.portfolio.feed.global.exception.CustomException;
import com.portfolio.feed.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RankingService rankingService;

    // Redis Set 키: 게시글 좋아요 누른 유저 집합
    // SADD feed:likes:post:{postId} {userId}
    private String likeKey(Long postId) { return "feed:likes:post:" + postId; }

    @Transactional
    public int like(Long userId, Long postId) {
        // Redis Set에 원자적으로 추가 (SADD — 이미 있으면 0 반환)
        // Long 1: 추가 성공, Long 0: 이미 존재
        Long added = redisTemplate.opsForSet().add(likeKey(postId), String.valueOf(userId));

        if (added == null || added == 0) {
            throw new CustomException(ErrorCode.ALREADY_LIKED);
        }

        // DB 저장 (비동기로 처리해도 되지만 단순화를 위해 동기 처리)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Post post = postRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        postLikeRepository.save(PostLike.builder().user(user).post(post).build());

        // Redis Set 크기 = 좋아요 수
        Long likeCount = redisTemplate.opsForSet().size(likeKey(postId));
        int count = likeCount != null ? likeCount.intValue() : 0;
        post.syncLikeCount(count);

        // 랭킹 점수 추가
        rankingService.addLikeScore(postId);

        return count;
    }

    @Transactional
    public int unlike(Long userId, Long postId) {
        Long removed = redisTemplate.opsForSet()
                .remove(likeKey(postId), String.valueOf(userId));

        if (removed == null || removed == 0) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }

        postLikeRepository.findByUserIdAndPostId(userId, postId)
                .ifPresent(postLikeRepository::delete);

        Post post = postRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Long likeCount = redisTemplate.opsForSet().size(likeKey(postId));
        int count = likeCount != null ? likeCount.intValue() : 0;
        post.syncLikeCount(count);

        rankingService.removeLikeScore(postId);

        return count;
    }

    public boolean isLiked(Long userId, Long postId) {
        Boolean result = redisTemplate.opsForSet()
                .isMember(likeKey(postId), String.valueOf(userId));
        return Boolean.TRUE.equals(result);
    }
}
