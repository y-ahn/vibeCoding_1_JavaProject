package com.portfolio.commerce.domain.payment.dto;

import com.portfolio.commerce.domain.payment.entity.Payment;
import com.portfolio.commerce.domain.payment.entity.PaymentStatus;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private String impUid;
    private String merchantUid;
    private Integer amount;
    private PaymentStatus status;
    private String payMethod;
    private LocalDateTime createdAt;

    public static PaymentResponse from(Payment payment) {
        PaymentResponse res = new PaymentResponse();
        res.id = payment.getId();
        res.orderId = payment.getOrder().getId();
        res.impUid = payment.getImpUid();
        res.merchantUid = payment.getMerchantUid();
        res.amount = payment.getAmount();
        res.status = payment.getStatus();
        res.payMethod = payment.getPayMethod();
        res.createdAt = payment.getCreatedAt();
        return res;
    }
}
