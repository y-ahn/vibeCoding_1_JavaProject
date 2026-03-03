package com.portfolio.commerce.domain.payment.service;

import com.portfolio.commerce.domain.order.entity.Order;
import com.portfolio.commerce.domain.order.repository.OrderRepository;
import com.portfolio.commerce.domain.payment.dto.*;
import com.portfolio.commerce.domain.payment.entity.*;
import com.portfolio.commerce.domain.payment.repository.PaymentRepository;
import com.portfolio.commerce.global.exception.CustomException;
import com.portfolio.commerce.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PortOneClient portOneClient;

    // 결제 검증 — 포트원 결제 완료 후 서버에서 검증
    // 클라이언트 → 포트원 결제 → impUid 전달 → 서버에서 포트원에 금액 확인
    @Transactional
    public PaymentResponse verifyAndComplete(Long userId, PaymentVerifyRequest req) {
        Order order = orderRepository.findByIdWithUser(req.getOrderId())
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        // 본인 주문인지 확인
        if (!order.getUser().getId().equals(userId))
            throw new CustomException(ErrorCode.FORBIDDEN);

        // 이미 결제된 주문인지 확인
        if (paymentRepository.findByOrderId(order.getId()).isPresent())
            throw new CustomException(ErrorCode.PAYMENT_ALREADY_COMPLETED);

        // ✅ 핵심: 포트원에서 실제 결제 정보 조회 (위변조 방지)
        PortOnePaymentResponse.PaymentData portonePayment = portOneClient.getPayment(req.getImpUid());

        // 결제 상태 확인
        if (!portonePayment.isPaid()) {
            throw new CustomException(ErrorCode.PAYMENT_VERIFICATION_FAILED);
        }

        // ✅ 금액 검증 — 클라이언트가 amount를 조작하면 여기서 잡힘
        if (!portonePayment.getAmount().equals(order.getTotalAmount())) {
            log.error("결제 금액 불일치 orderId={} expected={} actual={}",
                    order.getId(), order.getTotalAmount(), portonePayment.getAmount());
            throw new CustomException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        // 결제 정보 저장
        Payment payment = Payment.builder()
                .order(order)
                .impUid(req.getImpUid())
                .merchantUid(order.getMerchantUid())
                .amount(portonePayment.getAmount())
                .status(PaymentStatus.PAID)
                .payMethod(portonePayment.getPayMethod())
                .build();
        paymentRepository.save(payment);

        // 주문 상태 변경 PENDING → PAID
        order.pay();

        log.info("결제 검증 완료 orderId={} impUid={}", order.getId(), req.getImpUid());
        return PaymentResponse.from(payment);
    }

    public PaymentResponse getPayment(Long userId, Long orderId) {
        Order order = orderRepository.findByIdWithUser(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        if (!order.getUser().getId().equals(userId))
            throw new CustomException(ErrorCode.FORBIDDEN);

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
        return PaymentResponse.from(payment);
    }
}
