package com.portfolio.commerce.domain.product.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class ProductCreateRequest {
    @NotBlank @Size(max = 100) private String name;
    private String description;
    @NotNull @Min(0) private Integer price;
    @NotNull @Min(0) private Integer stock;
}
