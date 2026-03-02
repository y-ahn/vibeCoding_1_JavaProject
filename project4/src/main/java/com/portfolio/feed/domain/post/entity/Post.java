package com.portfolio.feed.domain.post.entity;

import com.portfolio.feed.domain.user.entity.User;
import com.portfolio.feed.global.response.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "posts",
    indexes = {
        // Cursor 페이징: WHERE author_id IN (...) AND id < cursor ORDER BY id DESC
        @Index(name = "idx_post_author_id", columnList = "author_id, id")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Post extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private int likeCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private int commentCount = 0;

    public void update(String content) { this.content = content; }

    // Redis와 동기화할 때 사용
    public void syncLikeCount(int count) { this.likeCount = count; }
    public void incrementCommentCount() { this.commentCount++; }
    public void decrementCommentCount() {
        if (this.commentCount > 0) this.commentCount--;
    }
}
