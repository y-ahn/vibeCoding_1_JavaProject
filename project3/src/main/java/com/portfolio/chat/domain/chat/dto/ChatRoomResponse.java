package com.portfolio.chat.domain.chat.dto;

import com.portfolio.chat.domain.chat.entity.ChatRoom;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ChatRoomResponse {
    private Long id;
    private Long opponentId;
    private String opponentNickname;
    private LocalDateTime createdAt;

    public static ChatRoomResponse from(ChatRoom room, Long myId) {
        ChatRoomResponse res = new ChatRoomResponse();
        res.id = room.getId();
        boolean isSender = room.getSender().getId().equals(myId);
        res.opponentId = isSender ? room.getReceiver().getId() : room.getSender().getId();
        res.opponentNickname = isSender
                ? room.getReceiver().getNickname()
                : room.getSender().getNickname();
        res.createdAt = room.getCreatedAt();
        return res;
    }
}
