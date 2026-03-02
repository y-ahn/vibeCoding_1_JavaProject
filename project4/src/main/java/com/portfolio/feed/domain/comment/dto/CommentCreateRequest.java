package com.portfolio.feed.domain.comment.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
@Getter
public class CommentCreateRequest {
    @NotBlank private String content;
    private Long parentId; // null이면 최상위 댓글, 값이 있으면 대댓글
}
