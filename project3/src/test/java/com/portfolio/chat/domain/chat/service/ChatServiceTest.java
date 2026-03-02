package com.portfolio.chat.domain.chat.service;

import com.portfolio.chat.domain.chat.dto.ChatRoomCreateRequest;
import com.portfolio.chat.domain.chat.entity.ChatRoom;
import com.portfolio.chat.domain.chat.repository.ChatMessageRepository;
import com.portfolio.chat.domain.chat.repository.ChatRoomRepository;
import com.portfolio.chat.domain.user.entity.User;
import com.portfolio.chat.domain.user.repository.UserRepository;
import com.portfolio.chat.global.exception.CustomException;
import com.portfolio.chat.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @InjectMocks ChatService chatService;
    @Mock ChatRoomRepository chatRoomRepository;
    @Mock ChatMessageRepository chatMessageRepository;
    @Mock UserRepository userRepository;
    @Mock ChatCacheService chatCacheService;
    @Mock ChatMessagePublisher publisher;

    @Test
    @DisplayName("채팅방 생성 — 이미 존재하면 기존 방 반환")
    void createOrGetRoom_existingRoom() {
        User sender = User.builder().id(1L).email("a@test.com")
                .password("pw").nickname("유저A").build();
        User receiver = User.builder().id(2L).email("b@test.com")
                .password("pw").nickname("유저B").build();
        ChatRoom existingRoom = ChatRoom.builder()
                .id(1L).sender(sender).receiver(receiver).build();

        ChatRoomCreateRequest req = new ChatRoomCreateRequest();

        given(chatRoomRepository.findByParticipants(1L, 2L))
                .willReturn(Optional.of(existingRoom));

        var result = chatService.createOrGetRoom(1L, req);

        assertThat(result.getId()).isEqualTo(1L);
        // 새 방을 생성하지 않아야 함
        verify(chatRoomRepository, never()).save(any());
    }

    @Test
    @DisplayName("채팅방 입장 — 참여자가 아닌 경우 FORBIDDEN")
    void enterRoom_forbidden() {
        User sender = User.builder().id(1L).email("a@test.com")
                .password("pw").nickname("유저A").build();
        User receiver = User.builder().id(2L).email("b@test.com")
                .password("pw").nickname("유저B").build();
        ChatRoom room = ChatRoom.builder().id(1L).sender(sender).receiver(receiver).build();

        given(chatRoomRepository.findById(1L)).willReturn(Optional.of(room));

        // userId=3L — 참여자가 아닌 유저
        assertThatThrownBy(() -> chatService.enterRoom(3L, 1L))
                .isInstanceOf(CustomException.class)
                .satisfies(e -> assertThat(((CustomException) e).getErrorCode())
                        .isEqualTo(ErrorCode.FORBIDDEN));
    }

    @Test
    @DisplayName("채팅방 입장 — 존재하지 않는 방")
    void enterRoom_notFound() {
        given(chatRoomRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> chatService.enterRoom(1L, 999L))
                .isInstanceOf(CustomException.class)
                .satisfies(e -> assertThat(((CustomException) e).getErrorCode())
                        .isEqualTo(ErrorCode.CHAT_ROOM_NOT_FOUND));
    }
}
