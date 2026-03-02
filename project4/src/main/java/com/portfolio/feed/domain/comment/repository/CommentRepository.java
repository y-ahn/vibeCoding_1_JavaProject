package com.portfolio.feed.domain.comment.repository;

import com.portfolio.feed.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 최상위 댓글만 조회 (parent == null)
    // 대댓글은 @BatchSize로 별도 IN 쿼리
    @Query("""
        SELECT c FROM Comment c JOIN FETCH c.author
        WHERE c.post.id = :postId AND c.parent IS NULL
        ORDER BY c.createdAt ASC
        """)
    List<Comment> findTopLevelByPostId(@Param("postId") Long postId);

    @Query("SELECT c FROM Comment c JOIN FETCH c.author WHERE c.id = :id")
    Optional<Comment> findByIdWithAuthor(@Param("id") Long id);
}
