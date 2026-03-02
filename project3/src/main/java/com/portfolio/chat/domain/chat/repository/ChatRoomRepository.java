package com.portfolio.chat.domain.chat.repository;

import com.portfolio.chat.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 이미 존재하는 채팅방 확인 (A→B, B→A 양방향)
    @Query("""
        SELECT r FROM ChatRoom r
        JOIN FETCH r.sender JOIN FETCH r.receiver
        WHERE (r.sender.id = :user1 AND r.receiver.id = :user2)
           OR (r.sender.id = :user2 AND r.receiver.id = :user1)
        """)
    Optional<ChatRoom> findByParticipants(@Param("user1") Long user1, @Param("user2") Long user2);

    // 내 채팅방 목록 (sender 또는 receiver로 참여)
    @Query("""
        SELECT r FROM ChatRoom r
        JOIN FETCH r.sender JOIN FETCH r.receiver
        WHERE r.sender.id = :userId OR r.receiver.id = :userId
        ORDER BY r.createdAt DESC
        """)
    List<ChatRoom> findMyRooms(@Param("userId") Long userId);
}
