package com.portfolio.market.domain.product.dto;

import com.portfolio.market.domain.product.entity.ProductCategory;
import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class ProductUpdateRequest {
    @NotBlank @Size(max = 100)
    private String title;
    @NotBlank
    private String description;
    @NotNull @Min(0)
    private Integer price;
    @NotNull
    private ProductCategory category;
}
