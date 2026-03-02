package com.portfolio.chat.global.jwt;

import com.portfolio.chat.global.exception.CustomException;
import com.portfolio.chat.global.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // STOMP CONNECT 명령어일 때만 JWT 검증
        // SUBSCRIBE, SEND 등의 명령어는 이미 인증된 Principal 재사용
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);
            try {
                Claims claims = Jwts.parser()
                        .verifyWith(key()).build()
                        .parseSignedClaims(token).getPayload();

                Long userId = Long.parseLong(claims.getSubject());
                String email = claims.get("email", String.class);

                // STOMP 세션에 사용자 정보 저장
                // 이후 @MessageMapping 메서드에서 Principal로 접근 가능
                accessor.setUser(new StompPrincipal(userId, email));
                log.debug("STOMP CONNECT 인증 성공: userId={}", userId);

            } catch (ExpiredJwtException e) {
                throw new CustomException(ErrorCode.TOKEN_EXPIRED);
            } catch (JwtException e) {
                throw new CustomException(ErrorCode.TOKEN_INVALID);
            }
        }
        return message;
    }
}
