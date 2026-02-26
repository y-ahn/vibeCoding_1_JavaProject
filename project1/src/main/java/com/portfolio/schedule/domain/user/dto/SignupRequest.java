package com.portfolio.schedule.domain.user.dto;
import jakarta.validation.constraints.*;
import lombok.*;
@Getter @NoArgsConstructor
public class SignupRequest {
    @NotBlank @Email private String email;
    @NotBlank @Size(min=8,message="비밀번호는 8자 이상") private String password;
    @NotBlank @Size(max=50) private String name;
}
