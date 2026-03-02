package com.portfolio.feed.domain.post.service;

import com.portfolio.feed.domain.post.dto.*;
import com.portfolio.feed.domain.post.entity.Post;
import com.portfolio.feed.domain.post.repository.PostRepository;
import com.portfolio.feed.domain.user.entity.User;
import com.portfolio.feed.domain.user.repository.FollowRepository;
import com.portfolio.feed.domain.user.repository.UserRepository;
import com.portfolio.feed.global.exception.CustomException;
import com.portfolio.feed.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    private static final int PAGE_SIZE = 10;

    @Transactional
    public PostResponse create(Long authorId, PostCreateRequest req) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Post post = Post.builder().author(author).content(req.getContent()).build();
        return PostResponse.from(postRepository.save(post));
    }

    public PostResponse getOne(Long postId) {
        return PostResponse.from(postRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND)));
    }

    // ✅ Cursor 기반 팔로우 피드
    // Offset 페이징 문제: OFFSET 1000이면 1000개 스캔 후 버림 → 느려짐
    // Cursor 방식: WHERE id < cursor → 인덱스 직접 탐색 → 항상 빠름
    public CursorResult<PostResponse> getFeed(Long userId, Long cursor) {
        List<Long> followingIds = followRepository.findFollowingIds(userId);
        if (followingIds.isEmpty()) return CursorResult.of(List.of(), null);

        // size+1 개를 조회해서 다음 페이지 존재 여부 확인
        List<Post> posts = postRepository.findFeedWithCursor(
                followingIds, cursor, PageRequest.of(0, PAGE_SIZE + 1));

        return buildCursorResult(posts);
    }

    // 내 게시글 목록 (Cursor)
    public CursorResult<PostResponse> getMyPosts(Long authorId, Long cursor) {
        List<Post> posts = postRepository.findByAuthorWithCursor(
                authorId, cursor, PageRequest.of(0, PAGE_SIZE + 1));
        return buildCursorResult(posts);
    }

    @Transactional
    public PostResponse update(Long userId, Long postId, PostCreateRequest req) {
        Post post = ownerCheck(userId, postId);
        post.update(req.getContent());
        return PostResponse.from(post);
    }

    @Transactional
    public void delete(Long userId, Long postId) {
        postRepository.delete(ownerCheck(userId, postId));
    }

    private CursorResult<PostResponse> buildCursorResult(List<Post> posts) {
        boolean hasNext = posts.size() > PAGE_SIZE;
        List<Post> content = hasNext ? posts.subList(0, PAGE_SIZE) : posts;
        Long nextCursor = hasNext ? content.get(content.size() - 1).getId() : null;
        return CursorResult.of(content.stream().map(PostResponse::from).toList(), nextCursor);
    }

    private Post ownerCheck(Long userId, Long postId) {
        Post post = postRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        if (!post.getAuthor().getId().equals(userId))
            throw new CustomException(ErrorCode.FORBIDDEN);
        return post;
    }
}
