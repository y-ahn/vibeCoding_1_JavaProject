package com.portfolio.commerce.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 포트원 GET /payments/{imp_uid} 응답
@Getter
@NoArgsConstructor
public class PortOnePaymentResponse {
    private int code;
    private String message;
    private PaymentData response;

    @Getter
    @NoArgsConstructor
    public static class PaymentData {
        @JsonProperty("imp_uid")
        private String impUid;

        @JsonProperty("merchant_uid")
        private String merchantUid;

        @JsonProperty("pay_method")
        private String payMethod;

        private Integer amount;
        private String status; // paid, failed, cancelled

        public boolean isPaid() { return "paid".equals(this.status); }
    }
}
