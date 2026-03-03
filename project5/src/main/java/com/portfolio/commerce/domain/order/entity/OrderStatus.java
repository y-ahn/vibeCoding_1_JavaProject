package com.portfolio.commerce.domain.order.entity;

// 주문 상태 머신
// PENDING → PAID → SHIPPING → DELIVERED
//        ↓
//     CANCELLED (PENDING 또는 PAID에서만 가능)
public enum OrderStatus {
    PENDING,    // 주문 생성 (결제 대기)
    PAID,       // 결제 완료
    SHIPPING,   // 배송 중
    DELIVERED,  // 배송 완료
    CANCELLED   // 주문 취소
}
