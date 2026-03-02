package com.portfolio.chat.domain.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.chat.domain.chat.dto.ChatMessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageSubscriber {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper redisObjectMapper;

    // Redis에서 메시지를 받으면 WebSocket으로 전달
    // 이 메서드는 RedisConfig의 MessageListenerAdapter에 의해 자동 호출됨
    // "chat:room:*" 패턴을 구독하고 있으므로 모든 채팅방 메시지를 수신
    public void onMessage(String message, String channel) {
        try {
            ChatMessagePayload payload = redisObjectMapper.readValue(message, ChatMessagePayload.class);
            Long roomId = payload.getRoomId();

            // WebSocket 구독자에게 브로드캐스트
            // /topic/chat/room/{roomId}를 구독한 클라이언트에게 전송
            messagingTemplate.convertAndSend("/topic/chat/room/" + roomId, payload);
            log.debug("WebSocket 전달 roomId={}", roomId);

        } catch (Exception e) {
            log.error("메시지 구독 처리 실패: {}", e.getMessage());
        }
    }
}
