package com.portfolio.commerce.domain.order.service;

import com.portfolio.commerce.domain.order.dto.*;
import com.portfolio.commerce.domain.order.entity.*;
import com.portfolio.commerce.domain.order.repository.OrderRepository;
import com.portfolio.commerce.domain.product.entity.Product;
import com.portfolio.commerce.domain.product.repository.ProductRepository;
import com.portfolio.commerce.domain.user.entity.User;
import com.portfolio.commerce.domain.user.repository.UserRepository;
import com.portfolio.commerce.global.exception.CustomException;
import com.portfolio.commerce.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderResponse createOrder(Long userId, OrderCreateRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 주문번호 생성 (포트원 merchantUid)
        String merchantUid = "ORDER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .shippingAddress(req.getShippingAddress())
                .merchantUid(merchantUid)
                .totalAmount(0)
                .build();

        int totalAmount = 0;

        for (OrderCreateRequest.OrderItemRequest itemReq : req.getItems()) {
            // ✅ 비관적 락으로 재고 조회 — SELECT FOR UPDATE
            // 여러 요청이 동시에 들어와도 한 번에 하나씩 처리
            Product product = productRepository.findByIdWithLock(itemReq.getProductId())
                    .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

            // 재고 차감 (재고 부족 시 OUT_OF_STOCK 예외)
            product.decreaseStock(itemReq.getQuantity());

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(product.getPrice()) // 주문 시점 가격 스냅샷
                    .build();

            order.addItem(item);
            totalAmount += item.getSubTotal();
        }

        // totalAmount 필드 업데이트를 위한 새 Order 생성
        Order savedOrder = orderRepository.save(Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .shippingAddress(req.getShippingAddress())
                .merchantUid(merchantUid)
                .totalAmount(totalAmount)
                .build());

        // 아이템을 savedOrder에 다시 연결
        for (OrderCreateRequest.OrderItemRequest itemReq : req.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
            OrderItem item = OrderItem.builder()
                    .order(savedOrder)
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();
            savedOrder.addItem(item);
        }

        log.info("주문 생성 완료 orderId={} merchantUid={}", savedOrder.getId(), merchantUid);
        return OrderResponse.from(savedOrder);
    }

    public Page<OrderResponse> getMyOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable).map(OrderResponse::from);
    }

    public OrderResponse getOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByIdWithUser(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        if (!order.getUser().getId().equals(userId))
            throw new CustomException(ErrorCode.FORBIDDEN);
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByIdWithUser(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
        if (!order.getUser().getId().equals(userId))
            throw new CustomException(ErrorCode.FORBIDDEN);

        order.cancel();

        // 재고 복구
        for (OrderItem item : order.getItems()) {
            item.getProduct().increaseStock(item.getQuantity());
        }

        log.info("주문 취소 완료 orderId={}", orderId);
        return OrderResponse.from(order);
    }
}
