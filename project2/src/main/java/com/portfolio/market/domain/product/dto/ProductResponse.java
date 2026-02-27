package com.portfolio.market.domain.product.dto;

import com.portfolio.market.domain.product.entity.Product;
import com.portfolio.market.domain.product.entity.ProductCategory;
import com.portfolio.market.domain.product.entity.ProductStatus;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ProductResponse {
    private Long id;
    private String title;
    private String description;
    private Integer price;
    private ProductStatus status;
    private ProductCategory category;
    private String sellerNickname;
    private String sellerEmail;
    private List<String> imageUrls;
    private LocalDateTime createdAt;

    public static ProductResponse from(Product product) {
        ProductResponse res = new ProductResponse();
        res.id = product.getId();
        res.title = product.getTitle();
        res.description = product.getDescription();
        res.price = product.getPrice();
        res.status = product.getStatus();
        res.category = product.getCategory();
        res.sellerNickname = product.getSeller().getNickname();
        res.sellerEmail = product.getSeller().getEmail();
        res.imageUrls = product.getImages().stream()
                .map(i -> i.getImageUrl()).toList();
        res.createdAt = product.getCreatedAt();
        return res;
    }
}
