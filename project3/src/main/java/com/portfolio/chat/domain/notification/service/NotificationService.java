package com.portfolio.chat.domain.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class NotificationService {

    // ConcurrentHashMap: 멀티스레드 환경에서 안전한 Map
    // key: userId, value: SseEmitter (SSE 연결)
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    // SSE 연결 생성 — 클라이언트가 /api/notifications/subscribe 호출 시
    // timeout: 30분 (0L이면 무제한)
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);

        // ⚠️ 연결 종료 시 반드시 Map에서 제거 — 메모리 누수 방지
        // 3가지 콜백 모두 등록해야 함
        emitter.onCompletion(() -> {
            emitters.remove(userId);
            log.debug("SSE 연결 완료 userId={}", userId);
        });
        emitter.onTimeout(() -> {
            emitters.remove(userId);
            log.debug("SSE 타임아웃 userId={}", userId);
        });
        emitter.onError(e -> {
            emitters.remove(userId);
            log.warn("SSE 오류 userId={}: {}", userId, e.getMessage());
        });

        emitters.put(userId, emitter);

        // 연결 직후 초기 이벤트 전송 (연결 확인용)
        try {
            emitter.send(SseEmitter.event().name("connect").data("연결 성공"));
        } catch (IOException e) {
            emitters.remove(userId);
        }

        return emitter;
    }

    // 알림 전송
    public void sendNotification(Long userId, String eventName, Object data) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) return; // 오프라인 유저는 무시

        try {
            emitter.send(SseEmitter.event().name(eventName).data(data));
        } catch (IOException e) {
            emitters.remove(userId);
            log.warn("SSE 전송 실패 userId={}", userId);
        }
    }

    // 새 메시지 알림 (채팅 메시지 수신 시 상대방에게)
    public void notifyNewMessage(Long receiverId, Long roomId, String senderNickname) {
        sendNotification(receiverId, "new-message",
                Map.of("roomId", roomId, "from", senderNickname));
    }

    public int getConnectedCount() {
        return emitters.size();
    }
}
