package com.portfolio.schedule.domain.category.dto;
import com.portfolio.schedule.domain.category.entity.Category;
import lombok.*;
@Getter @Builder
public class CategoryResponse {
    private Long id; private String name; private String color;
    public static CategoryResponse from(Category c){
        return CategoryResponse.builder().id(c.getId()).name(c.getName()).color(c.getColor()).build();
    }
}
