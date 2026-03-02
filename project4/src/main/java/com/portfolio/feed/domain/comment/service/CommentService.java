package com.portfolio.feed.domain.comment.service;

import com.portfolio.feed.domain.comment.dto.*;
import com.portfolio.feed.domain.comment.entity.Comment;
import com.portfolio.feed.domain.comment.repository.CommentRepository;
import com.portfolio.feed.domain.post.entity.Post;
import com.portfolio.feed.domain.post.repository.PostRepository;
import com.portfolio.feed.domain.post.service.RankingService;
import com.portfolio.feed.domain.user.entity.User;
import com.portfolio.feed.domain.user.repository.UserRepository;
import com.portfolio.feed.global.exception.CustomException;
import com.portfolio.feed.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service @RequiredArgsConstructor @Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final RankingService rankingService;

    @Transactional
    public CommentResponse create(Long authorId, Long postId, CommentCreateRequest req) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Post post = postRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Comment parent = null;
        if (req.getParentId() != null) {
            parent = commentRepository.findByIdWithAuthor(req.getParentId())
                    .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        }

        Comment comment = Comment.builder()
                .post(post).author(author)
                .content(req.getContent()).parent(parent).build();
        commentRepository.save(comment);

        // 게시글 댓글 수 증가 + 랭킹 점수 추가
        post.incrementCommentCount();
        rankingService.addCommentScore(postId);

        return CommentResponse.from(comment);
    }

    // 계층형 댓글 조회 (최상위 댓글 + 대댓글 @BatchSize)
    public List<CommentResponse> getComments(Long postId) {
        return commentRepository.findTopLevelByPostId(postId).stream()
                .map(CommentResponse::from).toList();
    }

    @Transactional
    public void delete(Long userId, Long commentId) {
        Comment comment = commentRepository.findByIdWithAuthor(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        if (!comment.getAuthor().getId().equals(userId))
            throw new CustomException(ErrorCode.FORBIDDEN);

        // 대댓글이 있으면 soft delete, 없으면 hard delete
        if (!comment.getChildren().isEmpty()) {
            comment.delete();
        } else {
            commentRepository.delete(comment);
        }

        comment.getPost().decrementCommentCount();
    }
}
