package com.portfolio.chat.global.jwt;

import java.security.Principal;

// STOMP 세션에 저장되는 사용자 정보
// record: Java 16+, 불변 데이터 클래스 (equals, hashCode, toString 자동 생성)
public record StompPrincipal(Long userId, String email) implements Principal {

    @Override
    public String getName() {
        return String.valueOf(userId);
    }
}
