package com.techit.global.auth.security.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    // JWT 토큰을 저장하는 변수
    private String token;
    // 인증된 사용자의 주체 (Principal)
    private Object principal;
    // 인증 자격 증명 (Credentials)
    private Object credentials;

    // 인증된 사용자에 대한 토큰 생성자
    public JwtAuthenticationToken(Collection<? extends GrantedAuthority> authorities, Object principal, Object credentials) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.setAuthenticated(true); // 인증된 상태로 설정
    }

    // 인증되지 않은 사용자에 대한 토큰 생성자
    public JwtAuthenticationToken(String token) {
        super(null);
        this.token = token;
        // 인증되지 않은 상태로 설정
        this.setAuthenticated(false);
    }

    // 자격 증명을 반환하는 메서드
    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    // 주체를 반환하는 메서드
    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
