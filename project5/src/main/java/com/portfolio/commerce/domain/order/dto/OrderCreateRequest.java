package com.portfolio.commerce.domain.order.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import java.util.List;

@Getter
public class OrderCreateRequest {
    @NotNull @Size(min = 1) private List<OrderItemRequest> items;
    @NotBlank private String shippingAddress;

    @Getter
    public static class OrderItemRequest {
        @NotNull private Long productId;
        @NotNull @Min(1) private Integer quantity;
    }
}
