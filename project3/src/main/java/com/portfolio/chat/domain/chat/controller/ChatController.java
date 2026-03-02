package com.portfolio.chat.domain.chat.controller;

import com.portfolio.chat.domain.chat.dto.*;
import com.portfolio.chat.domain.chat.service.ChatService;
import com.portfolio.chat.global.jwt.StompPrincipal;
import com.portfolio.chat.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // ── REST API ──────────────────────────────────────────────

    // 채팅방 생성 또는 기존 방 조회
    @PostMapping("/api/chat/rooms")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> createOrGetRoom(
            @AuthenticationPrincipal UserDetails ud,
            @Valid @RequestBody ChatRoomCreateRequest req) {
        Long userId = Long.parseLong(ud.getUsername());
        return ResponseEntity.ok(ApiResponse.success(
                chatService.createOrGetRoom(userId, req)));
    }

    // 내 채팅방 목록
    @GetMapping("/api/chat/rooms")
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> getMyRooms(
            @AuthenticationPrincipal UserDetails ud) {
        Long userId = Long.parseLong(ud.getUsername());
        return ResponseEntity.ok(ApiResponse.success(chatService.getMyRooms(userId)));
    }

    // 채팅방 입장 (이전 메시지 조회)
    @GetMapping("/api/chat/rooms/{roomId}/messages")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> enterRoom(
            @AuthenticationPrincipal UserDetails ud,
            @PathVariable Long roomId) {
        Long userId = Long.parseLong(ud.getUsername());
        return ResponseEntity.ok(ApiResponse.success(
                chatService.enterRoom(userId, roomId)));
    }

    // ── STOMP WebSocket ───────────────────────────────────────

    // 메시지 전송 — 클라이언트: /app/chat.send 로 발행
    // 서버 처리 후 Redis Pub/Sub → /topic/chat/room/{roomId} 로 브로드캐스트
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessagePayload payload, Principal principal) {
        StompPrincipal stompPrincipal = (StompPrincipal) principal;
        payload.setSenderId(stompPrincipal.userId());
        log.debug("메시지 수신 userId={} roomId={}", stompPrincipal.userId(), payload.getRoomId());
        chatService.processMessage(payload);
    }

    // 채팅방 입장 알림 — 클라이언트: /app/chat.enter 로 발행
    @MessageMapping("/chat.enter")
    public void enterRoom(@Payload ChatMessagePayload payload, Principal principal) {
        StompPrincipal stompPrincipal = (StompPrincipal) principal;
        payload.setSenderId(stompPrincipal.userId());
        payload.setType(ChatMessagePayload.MessageType.ENTER);
        payload.setContent(stompPrincipal.email() + "님이 입장했습니다.");
        chatService.processMessage(payload);
    }
}
