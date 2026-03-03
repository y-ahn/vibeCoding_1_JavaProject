package com.portfolio.commerce.domain.order.entity;

import com.portfolio.commerce.domain.user.entity.User;
import com.portfolio.commerce.global.exception.CustomException;
import com.portfolio.commerce.global.exception.ErrorCode;
import com.portfolio.commerce.global.response.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders",
    indexes = @Index(name = "idx_order_user", columnList = "user_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Order extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(nullable = false)
    private Integer totalAmount;

    @Column(length = 200)
    private String shippingAddress;

    @Column(length = 50)
    private String merchantUid; // 포트원 주문번호

    @BatchSize(size = 50)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    // 상태 전환 — 유효하지 않은 상태 전환 시 예외
    public void pay() {
        if (this.status != OrderStatus.PENDING)
            throw new CustomException(ErrorCode.PAYMENT_ALREADY_COMPLETED);
        this.status = OrderStatus.PAID;
    }

    public void ship() { this.status = OrderStatus.SHIPPING; }

    public void deliver() { this.status = OrderStatus.DELIVERED; }

    public void cancel() {
        if (this.status != OrderStatus.PENDING && this.status != OrderStatus.PAID)
            throw new CustomException(ErrorCode.ORDER_CANCEL_NOT_ALLOWED);
        this.status = OrderStatus.CANCELLED;
    }

    public boolean isCancelable() {
        return this.status == OrderStatus.PENDING || this.status == OrderStatus.PAID;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }
}
