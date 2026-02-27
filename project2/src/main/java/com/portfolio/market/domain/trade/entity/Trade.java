package com.portfolio.market.domain.trade.entity;

import com.portfolio.market.domain.product.entity.Product;
import com.portfolio.market.domain.user.entity.User;
import com.portfolio.market.global.response.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trades")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Trade extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 구매자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    // 상품 (판매자 정보는 product.seller로 접근)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer tradePrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TradeStatus status;

    public void complete() {
        this.status = TradeStatus.COMPLETED;
    }

    public void cancel() {
        this.status = TradeStatus.CANCELLED;
    }
}
