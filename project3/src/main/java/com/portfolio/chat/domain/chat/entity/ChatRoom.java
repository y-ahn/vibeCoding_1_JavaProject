package com.portfolio.chat.domain.chat.entity;

import com.portfolio.chat.domain.user.entity.User;
import com.portfolio.chat.global.response.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class ChatRoom extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1:1 채팅 — sender와 receiver로 방을 고유하게 식별
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    // 두 유저가 참여하는 채팅방인지 확인
    public boolean hasParticipant(Long userId) {
        return sender.getId().equals(userId) || receiver.getId().equals(userId);
    }

    // 상대방 ID 반환
    public Long getOpponentId(Long myId) {
        return sender.getId().equals(myId) ? receiver.getId() : sender.getId();
    }
}
