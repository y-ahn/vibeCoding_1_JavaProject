package com.portfolio.commerce.domain.payment.entity;

import com.portfolio.commerce.domain.order.entity.Order;
import com.portfolio.commerce.global.response.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payments",
    indexes = @Index(name = "idx_payment_imp_uid", columnList = "imp_uid"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Payment extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false, unique = true, length = 100)
    private String impUid; // 포트원 결제 고유번호

    @Column(nullable = false, length = 50)
    private String merchantUid; // 주문번호

    @Column(nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(length = 50)
    private String payMethod; // card, vbank 등

    public void complete() { this.status = PaymentStatus.PAID; }
    public void cancel() { this.status = PaymentStatus.CANCELLED; }
}
