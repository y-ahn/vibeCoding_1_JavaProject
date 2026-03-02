package com.portfolio.feed.domain.user.dto;
import lombok.*;
@Getter @AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
}
