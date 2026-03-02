package com.portfolio.chat.domain.chat.service;

import com.portfolio.chat.domain.chat.dto.ChatMessagePayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    // 최근 메시지 유지 개수
    private static final int MAX_CACHE_SIZE = 50;
    // 캐시 TTL (24시간)
    private static final long TTL_HOURS = 24;

    private String cacheKey(Long roomId) {
        return "chat:messages:" + roomId;
    }

    // 메시지 캐싱
    // leftPush: 최신 메시지를 맨 앞에 추가
    // trim(0, MAX-1): 최근 50개만 유지 (메모리 관리)
    public void cacheMessage(Long roomId, ChatMessagePayload payload) {
        String key = cacheKey(roomId);
        try {
            redisTemplate.opsForList().leftPush(key, payload);
            redisTemplate.opsForList().trim(key, 0, MAX_CACHE_SIZE - 1);
            redisTemplate.expire(key, TTL_HOURS, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("메시지 캐싱 실패 roomId={}: {}", roomId, e.getMessage());
        }
    }

    // 캐시된 메시지 조회 (최신순)
    @SuppressWarnings("unchecked")
    public List<ChatMessagePayload> getCachedMessages(Long roomId) {
        String key = cacheKey(roomId);
        try {
            List<Object> cached = redisTemplate.opsForList().range(key, 0, -1);
            if (cached == null || cached.isEmpty()) return Collections.emptyList();
            return cached.stream()
                    .filter(o -> o instanceof ChatMessagePayload)
                    .map(o -> (ChatMessagePayload) o)
                    .toList();
        } catch (Exception e) {
            log.warn("캐시 조회 실패 roomId={}: {}", roomId, e.getMessage());
            return Collections.emptyList();
        }
    }

    // 캐시 존재 여부 확인
    public boolean hasCachedMessages(Long roomId) {
        Long size = redisTemplate.opsForList().size(cacheKey(roomId));
        return size != null && size > 0;
    }
}
