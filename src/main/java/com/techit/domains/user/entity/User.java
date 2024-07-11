package com.techit.domains.user.entity;

import com.techit.domains.blog.entity.Blog;
import jakarta.persistence.*;
import lombok.*;

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
    public Long id; // 회원 번호

    @Column(nullable = false, length = 30, unique = true)
    public String username; // ID

    @Column(nullable = false, length = 100)
    private String password; // 비밀번호

    @Column(nullable = false, length = 10)
    private String name; // 사용자 이름

    @Column(nullable = false, length = 50, unique = true)
    private String email; // 이메일

    @Column(nullable = false, length = 20)
    private String nickname; // 닉네임

    @Column(nullable = false, updatable = false, name = "created_at") // updatable = false : column 수정 시 값 변경을 막는다
    private LocalDateTime createdAt; // 가입일

    @Column(nullable = false, name = "is_deactivated")
    private Boolean isDeactivated; // 탈퇴 여부

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Blog blog; // 블로그

    @Setter
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<UserRole> userRoles = new ArrayList<>(); // 권한

    // 팔로우
    // 좋아요
    // 탈퇴 여부
}
