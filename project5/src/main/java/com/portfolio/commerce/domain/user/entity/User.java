package com.portfolio.commerce.domain.user.entity;

import com.portfolio.commerce.global.response.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "users")
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder @AllArgsConstructor
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(length = 200)
    private String address;

    @Column(length = 20)
    private String phone;
}
