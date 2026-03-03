package com.portfolio.commerce.domain.order.service;

import com.portfolio.commerce.domain.order.dto.OrderCreateRequest;
import com.portfolio.commerce.domain.order.entity.Order;
import com.portfolio.commerce.domain.order.entity.OrderStatus;
import com.portfolio.commerce.domain.order.repository.OrderRepository;
import com.portfolio.commerce.domain.product.entity.Product;
import com.portfolio.commerce.domain.product.entity.ProductStatus;
import com.portfolio.commerce.domain.product.repository.ProductRepository;
import com.portfolio.commerce.domain.user.entity.User;
import com.portfolio.commerce.domain.user.repository.UserRepository;
import com.portfolio.commerce.global.exception.CustomException;
import com.portfolio.commerce.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks OrderService orderService;
    @Mock OrderRepository orderRepository;
    @Mock ProductRepository productRepository;
    @Mock UserRepository userRepository;

    @Test
    @DisplayName("주문 취소 — PAID 상태에서 취소 가능")
    void cancelOrder_paidStatus() {
        User user = User.builder().id(1L).email("a@test.com")
                .password("pw").nickname("테스터").build();
        Order order = Order.builder().id(1L).user(user)
                .status(OrderStatus.PAID).totalAmount(10000)
                .merchantUid("ORDER-TEST01").shippingAddress("서울시").build();

        given(orderRepository.findByIdWithUser(1L)).willReturn(Optional.of(order));

        var result = orderService.cancelOrder(1L, 1L);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("주문 취소 — SHIPPING 상태에서 취소 불가")
    void cancelOrder_shippingStatus_throws() {
        User user = User.builder().id(1L).email("a@test.com")
                .password("pw").nickname("테스터").build();
        Order order = Order.builder().id(1L).user(user)
                .status(OrderStatus.SHIPPING).totalAmount(10000)
                .merchantUid("ORDER-TEST02").shippingAddress("서울시").build();

        given(orderRepository.findByIdWithUser(1L)).willReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(1L, 1L))
                .isInstanceOf(CustomException.class)
                .satisfies(e -> assertThat(((CustomException) e).getErrorCode())
                        .isEqualTo(ErrorCode.ORDER_CANCEL_NOT_ALLOWED));
    }

    @Test
    @DisplayName("주문 조회 — 본인이 아닌 경우 FORBIDDEN")
    void getOrder_forbidden() {
        User user = User.builder().id(1L).email("a@test.com")
                .password("pw").nickname("테스터").build();
        Order order = Order.builder().id(1L).user(user)
                .status(OrderStatus.PENDING).totalAmount(10000)
                .merchantUid("ORDER-TEST03").shippingAddress("서울시").build();

        given(orderRepository.findByIdWithUser(1L)).willReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.getOrder(2L, 1L))
                .isInstanceOf(CustomException.class)
                .satisfies(e -> assertThat(((CustomException) e).getErrorCode())
                        .isEqualTo(ErrorCode.FORBIDDEN));
    }
}
