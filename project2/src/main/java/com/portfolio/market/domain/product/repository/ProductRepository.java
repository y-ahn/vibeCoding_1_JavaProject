package com.portfolio.market.domain.product.repository;

import com.portfolio.market.domain.product.entity.Product;
import com.portfolio.market.domain.product.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // ✅ Fetch Join — seller N+1 방지
    // LAZY인 seller를 JOIN FETCH로 한 번에 가져옴
    // 주의: Fetch Join + Pageable 함께 쓰면 CountQuery 분리 필요
    @Query(value = """
            SELECT p FROM Product p
            JOIN FETCH p.seller
            WHERE p.status != 'SOLD'
            """,
           countQuery = """
            SELECT COUNT(p) FROM Product p
            WHERE p.status != 'SOLD'
            """)
    Page<Product> findActiveProductsWithSeller(Pageable pageable);

    // 내 상품 목록 (판매자 기준)
    @Query("SELECT p FROM Product p JOIN FETCH p.seller WHERE p.seller.id = :sellerId")
    Page<Product> findBySellerIdWithSeller(@Param("sellerId") Long sellerId, Pageable pageable);

    // 단건 조회 — seller + images 한 번에 (이미지는 @BatchSize로 처리됨)
    @Query("SELECT p FROM Product p JOIN FETCH p.seller WHERE p.id = :id")
    java.util.Optional<Product> findByIdWithSeller(@Param("id") Long id);
}
