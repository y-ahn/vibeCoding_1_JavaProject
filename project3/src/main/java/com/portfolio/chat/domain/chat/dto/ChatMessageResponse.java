package com.portfolio.chat.domain.chat.dto;

import com.portfolio.chat.domain.chat.entity.ChatMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageResponse {
    private Long id;
    private Long senderId;
    private String senderNickname;
    private String content;
    private boolean isRead;
    private LocalDateTime createdAt;

    public static ChatMessageResponse from(ChatMessage msg) {
        ChatMessageResponse res = new ChatMessageResponse();
        res.id = msg.getId();
        res.senderId = msg.getSender().getId();
        res.senderNickname = msg.getSender().getNickname();
        res.content = msg.getContent();
        res.isRead = msg.isRead();
        res.createdAt = msg.getCreatedAt();
        return res;
    }
}
