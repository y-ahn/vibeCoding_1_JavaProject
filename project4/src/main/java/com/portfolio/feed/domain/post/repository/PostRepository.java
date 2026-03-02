package com.portfolio.feed.domain.post.repository;

import com.portfolio.feed.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // Fetch Join으로 author N+1 방지
    @Query("SELECT p FROM Post p JOIN FETCH p.author WHERE p.id = :id")
    Optional<Post> findByIdWithAuthor(@Param("id") Long id);

    // ✅ Cursor 기반 페이징 — 팔로우 피드
    // WHERE author_id IN (...) AND id < cursor ORDER BY id DESC LIMIT size
    // id < cursor: 커서보다 작은 ID → 더 오래된 게시글
    // ORDER BY id DESC: 최신순
    @Query("""
        SELECT p FROM Post p JOIN FETCH p.author
        WHERE p.author.id IN :authorIds
        AND (:cursor IS NULL OR p.id < :cursor)
        ORDER BY p.id DESC
        """)
    List<Post> findFeedWithCursor(
            @Param("authorIds") List<Long> authorIds,
            @Param("cursor") Long cursor,
            org.springframework.data.domain.Pageable pageable);

    // 내 게시글 Cursor 페이징
    @Query("""
        SELECT p FROM Post p JOIN FETCH p.author
        WHERE p.author.id = :authorId
        AND (:cursor IS NULL OR p.id < :cursor)
        ORDER BY p.id DESC
        """)
    List<Post> findByAuthorWithCursor(
            @Param("authorId") Long authorId,
            @Param("cursor") Long cursor,
            org.springframework.data.domain.Pageable pageable);
}
