package com.portfolio.market.domain.product.entity;

import com.portfolio.market.domain.product.dto.ProductUpdateRequest;
import com.portfolio.market.domain.user.entity.User;
import com.portfolio.market.global.response.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "products",
    indexes = {
        // 자주 쓰이는 쿼리 패턴에 맞는 복합 인덱스
        // WHERE status = ? ORDER BY created_at DESC
        @Index(name = "idx_product_status_created", columnList = "status, created_at"),
        // WHERE seller_id = ? — 내 상품 목록 조회
        @Index(name = "idx_product_seller", columnList = "seller_id")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Product extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductCategory category;

    // ⚠️ LAZY 필수 — EAGER로 바꾸면 항상 JOIN 발생 (N+1 원인)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    // @BatchSize: 이미지 목록 조회 시 IN 쿼리로 배치 처리 (N+1 방지)
    // e.g. WHERE product_id IN (1, 2, 3, ..., 100) — 단 1개의 쿼리
    @BatchSize(size = 100)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    // 비즈니스 메서드
    public void update(ProductUpdateRequest req) {
        this.title = req.getTitle();
        this.description = req.getDescription();
        this.price = req.getPrice();
        this.category = req.getCategory();
    }

    public void changeStatus(ProductStatus status) {
        this.status = status;
    }

    public void addImage(ProductImage image) {
        this.images.add(image);
        image.assignProduct(this);
    }

    public boolean isSeller(Long userId) {
        return this.seller.getId().equals(userId);
    }
}
