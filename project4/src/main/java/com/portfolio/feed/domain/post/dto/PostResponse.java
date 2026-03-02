package com.portfolio.feed.domain.post.dto;

import com.portfolio.feed.domain.post.entity.Post;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class PostResponse {
    private Long id;
    private String content;
    private String authorNickname;
    private Long authorId;
    private int likeCount;
    private int commentCount;
    private LocalDateTime createdAt;

    public static PostResponse from(Post post) {
        PostResponse res = new PostResponse();
        res.id = post.getId();
        res.content = post.getContent();
        res.authorNickname = post.getAuthor().getNickname();
        res.authorId = post.getAuthor().getId();
        res.likeCount = post.getLikeCount();
        res.commentCount = post.getCommentCount();
        res.createdAt = post.getCreatedAt();
        return res;
    }
}
