package com.portfolio.chat.domain.chat.dto;

import lombok.*;
import java.time.LocalDateTime;

// STOMP 메시지 페이로드 (WebSocket으로 주고받는 데이터)
// Redis Pub/Sub에도 이 DTO를 JSON으로 직렬화해서 사용
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessagePayload {
    private Long roomId;
    private Long senderId;
    private String senderNickname;
    private String content;
    private LocalDateTime createdAt;
    private MessageType type;

    public enum MessageType {
        TALK,   // 일반 메시지
        ENTER,  // 입장 알림
        LEAVE   // 퇴장 알림
    }
}
