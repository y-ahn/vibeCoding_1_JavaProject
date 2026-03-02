package com.portfolio.feed.domain.user.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
@Getter
public class SignupRequest {
    @Email @NotBlank private String email;
    @NotBlank @Size(min = 8) private String password;
    @NotBlank @Size(min = 2, max = 20) private String nickname;
}
