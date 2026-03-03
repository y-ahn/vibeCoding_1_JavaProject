package com.portfolio.commerce.domain.product.repository;

import com.portfolio.commerce.domain.product.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // ✅ 비관적 락(Pessimistic Lock)
    // SELECT ... FOR UPDATE — 해당 row를 Lock으로 잠금
    // 다른 트랜잭션이 이 row를 수정하려면 현재 트랜잭션이 끝날 때까지 대기
    // 사용 이유: 재고 차감은 읽기-수정-쓰기 패턴 → 동시 요청 시 재고가 음수 될 수 있음
    // 낙관적 락(Optimistic)과의 차이:
    //   낙관적: 충돌 발생 시 예외 → 재시도 필요 (트래픽 많을 때 부담)
    //   비관적: 아예 잠가버림 → 데이터 정합성 확실, 단 성능 저하 가능
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithLock(@Param("id") Long id);

    Page<Product> findByStatusNot(
            com.portfolio.commerce.domain.product.entity.ProductStatus status,
            Pageable pageable);
}
