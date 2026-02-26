package com.portfolio.schedule.domain.schedule.dto;
import com.portfolio.schedule.domain.schedule.entity.Schedule;
import lombok.*;
import java.time.*;
@Getter @Builder
public class ScheduleResponse {
    private Long id; private String title; private String description;
    private LocalDate startDate; private LocalDate endDate; private boolean completed;
    private String categoryName; private String categoryColor; private LocalDateTime createdAt;
    public static ScheduleResponse from(Schedule s){
        return ScheduleResponse.builder().id(s.getId()).title(s.getTitle())
            .description(s.getDescription()).startDate(s.getStartDate())
            .endDate(s.getEndDate()).completed(s.isCompleted())
            .categoryName(s.getCategory()!=null?s.getCategory().getName():null)
            .categoryColor(s.getCategory()!=null?s.getCategory().getColor():null)
            .createdAt(s.getCreatedAt()).build();
    }
}
