package com.portfolio.commerce.domain.product.dto;

import com.portfolio.commerce.domain.product.entity.Product;
import com.portfolio.commerce.domain.product.entity.ProductStatus;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private Integer price;
    private Integer stock;
    private ProductStatus status;
    private LocalDateTime createdAt;

    public static ProductResponse from(Product p) {
        ProductResponse res = new ProductResponse();
        res.id = p.getId();
        res.name = p.getName();
        res.description = p.getDescription();
        res.price = p.getPrice();
        res.stock = p.getStock();
        res.status = p.getStatus();
        res.createdAt = p.getCreatedAt();
        return res;
    }
}
