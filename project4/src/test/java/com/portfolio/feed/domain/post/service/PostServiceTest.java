package com.portfolio.feed.domain.post.service;

import com.portfolio.feed.domain.post.dto.PostCreateRequest;
import com.portfolio.feed.domain.post.entity.Post;
import com.portfolio.feed.domain.post.repository.PostRepository;
import com.portfolio.feed.domain.user.entity.User;
import com.portfolio.feed.domain.user.repository.FollowRepository;
import com.portfolio.feed.domain.user.repository.UserRepository;
import com.portfolio.feed.global.exception.CustomException;
import com.portfolio.feed.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks PostService postService;
    @Mock PostRepository postRepository;
    @Mock UserRepository userRepository;
    @Mock FollowRepository followRepository;

    @Test
    @DisplayName("게시글 단건 조회 — 정상")
    void getOne_success() {
        User author = User.builder().id(1L).email("a@test.com")
                .password("pw").nickname("작성자").build();
        Post post = Post.builder().id(1L).author(author).content("안녕하세요").build();
        given(postRepository.findByIdWithAuthor(1L)).willReturn(Optional.of(post));

        var result = postService.getOne(1L);

        assertThat(result.getContent()).isEqualTo("안녕하세요");
        assertThat(result.getAuthorNickname()).isEqualTo("작성자");
    }

    @Test
    @DisplayName("게시글 조회 — 존재하지 않는 경우 POST_NOT_FOUND")
    void getOne_notFound() {
        given(postRepository.findByIdWithAuthor(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getOne(999L))
                .isInstanceOf(CustomException.class)
                .satisfies(e -> assertThat(((CustomException) e).getErrorCode())
                        .isEqualTo(ErrorCode.POST_NOT_FOUND));
    }

    @Test
    @DisplayName("피드 조회 — 팔로우 없으면 빈 목록")
    void getFeed_noFollowing() {
        given(followRepository.findFollowingIds(1L)).willReturn(List.of());

        var result = postService.getFeed(1L, null);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.isHasNext()).isFalse();
    }

    @Test
    @DisplayName("게시글 수정 — 본인이 아닌 경우 FORBIDDEN")
    void update_forbidden() {
        User author = User.builder().id(1L).email("a@test.com")
                .password("pw").nickname("작성자").build();
        Post post = Post.builder().id(1L).author(author).content("원본").build();
        given(postRepository.findByIdWithAuthor(1L)).willReturn(Optional.of(post));

        PostCreateRequest req = new PostCreateRequest();

        assertThatThrownBy(() -> postService.update(2L, 1L, req))
                .isInstanceOf(CustomException.class)
                .satisfies(e -> assertThat(((CustomException) e).getErrorCode())
                        .isEqualTo(ErrorCode.FORBIDDEN));
    }
}
