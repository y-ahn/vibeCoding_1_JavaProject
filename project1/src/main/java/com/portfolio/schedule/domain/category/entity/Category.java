package com.portfolio.schedule.domain.category.entity;
import com.portfolio.schedule.domain.user.entity.User;
import com.portfolio.schedule.global.response.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name="categories") @Getter @NoArgsConstructor(access=AccessLevel.PROTECTED)
public class Category extends BaseTimeEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id",nullable=false) private User user;
    @Column(nullable=false,length=50) private String name;
    @Column(length=7) private String color;
    @Builder public Category(User user,String name,String color){this.user=user;this.name=name;this.color=color;}
    public void update(String name,String color){this.name=name;this.color=color;}
}
