package com.portfolio.chat.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    UNAUTHORIZED(401, "인증이 필요합니다"),
    TOKEN_EXPIRED(401, "토큰이 만료되었습니다"),
    TOKEN_INVALID(401, "유효하지 않은 토큰입니다"),
    FORBIDDEN(403, "접근 권한이 없습니다"),
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다"),
    EMAIL_ALREADY_EXISTS(409, "이미 사용 중인 이메일입니다"),
    INVALID_PASSWORD(400, "비밀번호가 올바르지 않습니다"),
    CHAT_ROOM_NOT_FOUND(404, "채팅방을 찾을 수 없습니다"),
    CHAT_ROOM_ALREADY_EXISTS(409, "이미 존재하는 채팅방입니다"),
    INVALID_INPUT(400, "잘못된 입력값입니다"),
    INTERNAL_SERVER_ERROR(500, "서버 오류가 발생했습니다");

    private final int status;
    private final String message;
}
