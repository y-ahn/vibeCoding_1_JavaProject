package com.portfolio.chat.domain.chat.service;

import com.portfolio.chat.domain.chat.dto.ChatMessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    // Redis Pub/Sub 채널로 메시지 발행
    // 채널명: chat:room:{roomId}
    // 구독한 모든 서버 인스턴스가 메시지를 수신함
    public void publish(Long roomId, ChatMessagePayload payload) {
        String channel = "chat:room:" + roomId;
        try {
            redisTemplate.convertAndSend(channel, payload);
            log.debug("메시지 발행 channel={}", channel);
        } catch (Exception e) {
            log.error("Redis 발행 실패 channel={}: {}", channel, e.getMessage());
        }
    }
}
