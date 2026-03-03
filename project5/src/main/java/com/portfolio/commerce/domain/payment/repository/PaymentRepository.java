package com.portfolio.commerce.domain.payment.repository;

import com.portfolio.commerce.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByImpUid(String impUid);
    Optional<Payment> findByOrderId(Long orderId);
}
