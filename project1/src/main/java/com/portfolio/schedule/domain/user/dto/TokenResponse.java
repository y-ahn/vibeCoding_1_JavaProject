package com.portfolio.schedule.domain.user.dto;
import lombok.*;
@Getter @AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType="Bearer";
    public TokenResponse(String a,String r){this.accessToken=a;this.refreshToken=r;}
}
