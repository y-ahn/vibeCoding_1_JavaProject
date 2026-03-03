package com.portfolio.commerce.domain.product.entity;

import com.portfolio.commerce.global.response.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Product extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer price;

    // ⚠️ 비관적 락(Pessimistic Lock) 대상 필드
    // 동시에 여러 요청이 재고를 차감할 때 데이터 정합성 보장
    @Column(nullable = false)
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status;

    public void decreaseStock(int quantity) {
        if (this.stock < quantity) {
            throw new com.portfolio.commerce.global.exception.CustomException(
                    com.portfolio.commerce.global.exception.ErrorCode.OUT_OF_STOCK);
        }
        this.stock -= quantity;
    }

    public void increaseStock(int quantity) {
        this.stock += quantity;
    }

    public void updateInfo(String name, String description, Integer price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
