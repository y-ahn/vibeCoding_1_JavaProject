package com.portfolio.feed.domain.comment.dto;

import com.portfolio.feed.domain.comment.entity.Comment;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CommentResponse {
    private Long id;
    private String content;
    private Long authorId;
    private String authorNickname;
    private boolean deleted;
    private List<CommentResponse> children;
    private LocalDateTime createdAt;

    public static CommentResponse from(Comment comment) {
        CommentResponse res = new CommentResponse();
        res.id = comment.getId();
        res.content = comment.getContent();
        res.authorId = comment.getAuthor().getId();
        res.authorNickname = comment.getAuthor().getNickname();
        res.deleted = comment.isDeleted();
        res.createdAt = comment.getCreatedAt();
        res.children = comment.getChildren().stream()
                .map(CommentResponse::from).toList();
        return res;
    }
}
