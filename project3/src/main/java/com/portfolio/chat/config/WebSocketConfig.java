package com.portfolio.chat.config;

import com.portfolio.chat.global.jwt.StompAuthChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompAuthChannelInterceptor stompAuthChannelInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 구독 prefix — 클라이언트가 메시지를 받을 때 사용
        // /topic  : 1:N 브로드캐스트 (채팅방 전체)
        // /queue  : 1:1 개인 메시지
        registry.enableSimpleBroker("/topic", "/queue");

        // 발행 prefix — 클라이언트가 메시지를 보낼 때 사용
        // /app/chat.send → @MessageMapping("/chat.send") 메서드로 라우팅
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 연결 엔드포인트
        // SockJS: WebSocket 미지원 브라우저를 위한 폴백 (HTTP Long Polling 등)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // STOMP CONNECT 시점에 JWT 검증
        // HTTP Filter가 아닌 STOMP 레벨에서 인증하는 이유:
        // WebSocket은 최초 HTTP Upgrade 이후 HTTP 헤더를 사용하지 않음
        // STOMP CONNECT 프레임의 헤더에서 JWT를 추출해야 함
        registration.interceptors(stompAuthChannelInterceptor);
    }
}
