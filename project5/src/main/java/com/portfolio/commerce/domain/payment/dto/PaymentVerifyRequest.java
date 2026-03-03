package com.portfolio.commerce.domain.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PaymentVerifyRequest {
    @NotBlank private String impUid;      // 포트원 결제 고유번호
    @NotNull private Long orderId;        // 우리 서버 주문 ID
}
