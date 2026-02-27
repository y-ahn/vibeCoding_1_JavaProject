package com.portfolio.market.domain.trade.repository;

import com.portfolio.market.domain.trade.entity.Trade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    // 구매 내역 조회 (buyer 기준) — buyer + product + seller Fetch Join
    @Query(value = """
            SELECT t FROM Trade t
            JOIN FETCH t.buyer
            JOIN FETCH t.product p
            JOIN FETCH p.seller
            WHERE t.buyer.id = :buyerId
            """,
           countQuery = "SELECT COUNT(t) FROM Trade t WHERE t.buyer.id = :buyerId")
    Page<Trade> findByBuyerIdWithDetails(@Param("buyerId") Long buyerId, Pageable pageable);

    // 판매 내역 조회 (seller 기준)
    @Query(value = """
            SELECT t FROM Trade t
            JOIN FETCH t.buyer
            JOIN FETCH t.product p
            JOIN FETCH p.seller
            WHERE p.seller.id = :sellerId
            """,
           countQuery = """
            SELECT COUNT(t) FROM Trade t
            WHERE t.product.seller.id = :sellerId
            """)
    Page<Trade> findBySellerIdWithDetails(@Param("sellerId") Long sellerId, Pageable pageable);
}
