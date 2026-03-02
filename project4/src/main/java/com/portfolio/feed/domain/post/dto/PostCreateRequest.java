package com.portfolio.feed.domain.post.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class PostCreateRequest {
    @NotBlank private String content;
}
