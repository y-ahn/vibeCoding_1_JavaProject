package com.portfolio.schedule.domain.schedule.entity;
import com.portfolio.schedule.domain.category.entity.Category;
import com.portfolio.schedule.domain.schedule.dto.ScheduleRequest;
import com.portfolio.schedule.domain.user.entity.User;
import com.portfolio.schedule.global.response.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
@Entity @Table(name="schedules") @Getter @NoArgsConstructor(access=AccessLevel.PROTECTED)
public class Schedule extends BaseTimeEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id",nullable=false) private User user;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="category_id") private Category category;
    @Column(nullable=false,length=100) private String title;
    @Column(columnDefinition="TEXT") private String description;
    @Column(nullable=false) private LocalDate startDate;
    private LocalDate endDate;
    @Column(nullable=false) private boolean completed=false;
    @Builder
    public Schedule(User user,Category category,String title,String description,LocalDate startDate,LocalDate endDate){
        this.user=user;this.category=category;this.title=title;
        this.description=description;this.startDate=startDate;this.endDate=endDate;
    }
    public void update(ScheduleRequest req){
        this.title=req.getTitle();this.description=req.getDescription();
        this.startDate=req.getStartDate();this.endDate=req.getEndDate();
    }
    public void toggleCompleted(){this.completed=!this.completed;}
}
