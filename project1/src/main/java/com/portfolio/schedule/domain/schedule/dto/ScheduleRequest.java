package com.portfolio.schedule.domain.schedule.dto;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
@Getter @NoArgsConstructor
public class ScheduleRequest {
    @NotBlank(message="제목 필수") @Size(max=100) private String title;
    private String description;
    @NotNull(message="시작일 필수") private LocalDate startDate;
    private LocalDate endDate;
    private Long categoryId;
}
