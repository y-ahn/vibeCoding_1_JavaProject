package com.portfolio.commerce.global.exception;

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
    PRODUCT_NOT_FOUND(404, "상품을 찾을 수 없습니다"),
    OUT_OF_STOCK(409, "재고가 부족합니다"),
    ORDER_NOT_FOUND(404, "주문을 찾을 수 없습니다"),
    ORDER_CANCEL_NOT_ALLOWED(400, "취소할 수 없는 주문 상태입니다"),
    PAYMENT_NOT_FOUND(404, "결제 정보를 찾을 수 없습니다"),
    PAYMENT_AMOUNT_MISMATCH(400, "결제 금액이 일치하지 않습니다"),
    PAYMENT_ALREADY_COMPLETED(409, "이미 완료된 결제입니다"),
    PAYMENT_VERIFICATION_FAILED(400, "결제 검증에 실패했습니다"),
    PORTONE_API_ERROR(500, "포트원 API 오류가 발생했습니다"),
    INVALID_INPUT(400, "잘못된 입력값입니다"),
    INTERNAL_SERVER_ERROR(500, "서버 오류가 발생했습니다");

    private final int status;
    private final String message;
}
