package com.portfolio.commerce.domain.order.dto;

import com.portfolio.commerce.domain.order.entity.Order;
import com.portfolio.commerce.domain.order.entity.OrderStatus;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderResponse {
    private Long id;
    private OrderStatus status;
    private Integer totalAmount;
    private String shippingAddress;
    private String merchantUid;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;

    public static OrderResponse from(Order order) {
        OrderResponse res = new OrderResponse();
        res.id = order.getId();
        res.status = order.getStatus();
        res.totalAmount = order.getTotalAmount();
        res.shippingAddress = order.getShippingAddress();
        res.merchantUid = order.getMerchantUid();
        res.items = order.getItems().stream().map(i -> {
            OrderItemResponse ir = new OrderItemResponse();
            ir.productId = i.getProduct().getId();
            ir.productName = i.getProduct().getName();
            ir.quantity = i.getQuantity();
            ir.unitPrice = i.getUnitPrice();
            ir.subTotal = i.getSubTotal();
            return ir;
        }).toList();
        res.createdAt = order.getCreatedAt();
        return res;
    }

    @Getter
    public static class OrderItemResponse {
        Long productId;
        String productName;
        Integer quantity;
        Integer unitPrice;
        Integer subTotal;
    }
}
