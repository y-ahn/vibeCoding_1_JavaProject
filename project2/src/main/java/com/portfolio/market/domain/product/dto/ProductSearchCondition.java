package com.portfolio.market.domain.product.dto;

import com.portfolio.market.domain.product.entity.ProductCategory;
import com.portfolio.market.domain.product.entity.ProductStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSearchCondition {
    private String keyword;       // 제목 검색
    private ProductCategory category;
    private Integer minPrice;
    private Integer maxPrice;
    private ProductStatus status; // null이면 전체 (SOLD 제외가 기본)
}
