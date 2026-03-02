package com.portfolio.chat.domain.chat.repository;

import com.portfolio.chat.domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 채팅방 메시지 페이지네이션 (최신순)
    @Query(value = """
        SELECT m FROM ChatMessage m
        JOIN FETCH m.sender
        WHERE m.room.id = :roomId
        """,
        countQuery = "SELECT COUNT(m) FROM ChatMessage m WHERE m.room.id = :roomId")
    Page<ChatMessage> findByRoomId(@Param("roomId") Long roomId, Pageable pageable);

    // 안 읽은 메시지 일괄 읽음 처리
    @Modifying
    @Query("""
        UPDATE ChatMessage m SET m.isRead = true
        WHERE m.room.id = :roomId AND m.sender.id != :userId AND m.isRead = false
        """)
    int markAllAsRead(@Param("roomId") Long roomId, @Param("userId") Long userId);

    // 최근 메시지 N개 (Redis 캐시 초기 로드용)
    @Query("""
        SELECT m FROM ChatMessage m
        JOIN FETCH m.sender
        WHERE m.room.id = :roomId
        ORDER BY m.createdAt DESC
        """)
    List<ChatMessage> findRecentMessages(@Param("roomId") Long roomId, Pageable pageable);
}
