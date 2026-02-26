package com.portfolio.schedule.domain.user.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
@Getter @NoArgsConstructor
public class LoginRequest {
    @NotBlank private String email;
    @NotBlank private String password;
}
