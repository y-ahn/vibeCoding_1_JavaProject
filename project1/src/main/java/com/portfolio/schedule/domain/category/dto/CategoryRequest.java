package com.portfolio.schedule.domain.category.dto;
import jakarta.validation.constraints.*;
import lombok.*;
@Getter @NoArgsConstructor
public class CategoryRequest {
    @NotBlank @Size(max=50) private String name;
    private String color;
}
