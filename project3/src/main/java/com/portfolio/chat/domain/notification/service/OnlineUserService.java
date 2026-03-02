package com.portfolio.chat.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OnlineUserService {

    private final RedisTemplate<String, Object> redisTemplate;

    // Redis Set — 중복 없는 집합 자료구조
    // Set을 선택한 이유: 같은 유저가 여러 탭에서 접속해도 중복 없이 관리
    private static final long TTL_HOURS = 2;

    private String key(Long roomId) { return "chat:online:" + roomId; }

    // 채팅방 입장
    public void enter(Long roomId, Long userId) {
        try {
            redisTemplate.opsForSet().add(key(roomId), String.valueOf(userId));
            redisTemplate.expire(key(roomId), TTL_HOURS, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("온라인 입장 실패: {}", e.getMessage());
        }
    }

    // 채팅방 퇴장
    public void leave(Long roomId, Long userId) {
        try {
            redisTemplate.opsForSet().remove(key(roomId), String.valueOf(userId));
        } catch (Exception e) {
            log.warn("온라인 퇴장 실패: {}", e.getMessage());
        }
    }

    // 온라인 인원 수
    public Long countOnline(Long roomId) {
        try {
            Long count = redisTemplate.opsForSet().size(key(roomId));
            return count != null ? count : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    // 특정 유저 온라인 여부
    public boolean isOnline(Long roomId, Long userId) {
        try {
            Boolean result = redisTemplate.opsForSet()
                    .isMember(key(roomId), String.valueOf(userId));
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            return false;
        }
    }
}
