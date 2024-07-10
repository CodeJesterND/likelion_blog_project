package com.techit.global.auth.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    // 사용자 ID
    private final Long userId;
    // 사용자 이름 (로그인 ID)
    private final String username;
    // 사용자 비밀번호
    private final String password;
    // 사용자 실제 이름
    private final String name;
    // 사용자의 권한 목록
    private final List<GrantedAuthority> authorities;
    // 블로그 ID
    private final Long blogId;

    // 생성자: 사용자 정보와 권한 목록을 설정
    public CustomUserDetails(Long userId, String username, String password, String name, List<String> roles, Long blogId) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()); // 역할 목록을 GrantedAuthority 객체로 변환하여 설정
        this.blogId = blogId;
    }

    // 사용자 ID 반환
    public Long getUserId() {
        return userId;
    }

    // 로그인 ID 반환 (UserDetails 인터페이스 메서드)
    @Override
    public String getUsername() {
        return username;
    }

    // 비밀번호 반환 (UserDetails 인터페이스 메서드)
    @Override
    public String getPassword() {
        return password;
    }

    // 사용자 실제 이름 반환
    public String getName() {
        return name;
    }

    // 권한 목록 반환 (UserDetails 인터페이스 메서드)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // 블로그 ID 반환
    public Long getBlogId() {
        return blogId;
    }

    // 계정이 만료되지 않았는지 여부 반환 (UserDetails 인터페이스 메서드)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정이 잠겨있지 않은지 여부 반환 (UserDetails 인터페이스 메서드)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 자격 증명이 만료되지 않았는지 여부 반환 (UserDetails 인터페이스 메서드)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정이 활성화되었는지 여부 반환 (UserDetails 인터페이스 메서드)
    @Override
    public boolean isEnabled() {
        return true;
    }
}