package com.techit.domains.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    public Long userId; // 회원 번호

    @Column(nullable = false, length = 30, unique = true)
    public String username; // ID

    @Column(nullable = false, length = 100)
    private String password; // 비밀번호

    @Column(nullable = false, length = 30)
    private String name; // 사용자 이름

    @Column(nullable = false, length = 255, unique = true)
    private String email; // 이메일

    @Column(nullable = false, length = 30, unique = true)
    private String nickname; // 닉네임

    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt; // 가입일

    @OneToMany(mappedBy = "user")
    private List<UserRole> userRoles = new ArrayList<>();

    // 블로그
    // 팔로우
    // 좋아요
    // 탈퇴 여부
}
