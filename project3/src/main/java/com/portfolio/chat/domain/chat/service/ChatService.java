package com.portfolio.chat.domain.chat.service;

import com.portfolio.chat.domain.chat.dto.*;
import com.portfolio.chat.domain.chat.entity.ChatMessage;
import com.portfolio.chat.domain.chat.entity.ChatRoom;
import com.portfolio.chat.domain.chat.repository.ChatMessageRepository;
import com.portfolio.chat.domain.chat.repository.ChatRoomRepository;
import com.portfolio.chat.domain.user.entity.User;
import com.portfolio.chat.domain.user.repository.UserRepository;
import com.portfolio.chat.global.exception.CustomException;
import com.portfolio.chat.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatCacheService chatCacheService;
    private final ChatMessagePublisher publisher;

    // 채팅방 생성 (이미 있으면 기존 방 반환)
    @Transactional
    public ChatRoomResponse createOrGetRoom(Long myId, ChatRoomCreateRequest req) {
        return chatRoomRepository.findByParticipants(myId, req.getReceiverId())
                .map(room -> ChatRoomResponse.from(room, myId))
                .orElseGet(() -> {
                    User me = userRepository.findById(myId)
                            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
                    User receiver = userRepository.findById(req.getReceiverId())
                            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
                    ChatRoom room = ChatRoom.builder().sender(me).receiver(receiver).build();
                    return ChatRoomResponse.from(chatRoomRepository.save(room), myId);
                });
    }

    // 내 채팅방 목록
    public List<ChatRoomResponse> getMyRooms(Long userId) {
        return chatRoomRepository.findMyRooms(userId).stream()
                .map(room -> ChatRoomResponse.from(room, userId)).toList();
    }

    // 채팅방 입장 — 메시지 기록 조회 (캐시 우선, 없으면 DB)
    @Transactional
    public List<ChatMessageResponse> enterRoom(Long userId, Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        if (!room.hasParticipant(userId)) throw new CustomException(ErrorCode.FORBIDDEN);

        // 읽음 처리
        chatMessageRepository.markAllAsRead(roomId, userId);

        // 캐시에 있으면 캐시 반환, 없으면 DB 조회
        if (chatCacheService.hasCachedMessages(roomId)) {
            return chatCacheService.getCachedMessages(roomId).stream()
                    .map(p -> {
                        ChatMessageResponse res = new ChatMessageResponse();
                        return res;
                    }).toList();
        }

        return chatMessageRepository.findRecentMessages(roomId,
                        PageRequest.of(0, 50, Sort.by("createdAt").descending()))
                .stream().map(ChatMessageResponse::from).toList();
    }

    // STOMP 메시지 처리 — 저장 + Redis 발행
    @Transactional
    public void processMessage(ChatMessagePayload payload) {
        ChatRoom room = chatRoomRepository.findById(payload.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));
        User sender = userRepository.findById(payload.getSenderId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // DB 저장
        ChatMessage message = ChatMessage.builder()
                .room(room).sender(sender)
                .content(payload.getContent()).isRead(false).build();
        chatMessageRepository.save(message);

        // 발행할 payload에 시간 + 닉네임 설정
        payload.setSenderNickname(sender.getNickname());
        payload.setCreatedAt(LocalDateTime.now());
        payload.setType(ChatMessagePayload.MessageType.TALK);

        // Redis 캐시 저장
        chatCacheService.cacheMessage(room.getId(), payload);

        // Redis Pub/Sub 발행 → 모든 서버 인스턴스의 Subscriber가 수신
        publisher.publish(room.getId(), payload);
    }
}
