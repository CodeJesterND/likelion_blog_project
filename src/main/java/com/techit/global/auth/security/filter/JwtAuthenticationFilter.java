package com.techit.global.auth.security.filter;


import com.techit.global.auth.jwt.service.TokenRenewalService;
import com.techit.global.auth.security.CustomUserDetails;
import com.techit.global.auth.jwt.util.CookieHelper;
import com.techit.global.auth.jwt.util.JwtTokenizer;
import com.techit.global.auth.security.token.JwtAuthenticationToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenizer jwtTokenizer;
    private final TokenRenewalService tokenRenewalService;
    private final CookieHelper cookieHelper;

    // 요청을 필터링하는 메서드
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 요청에서 accessToken을 추출
        String token = getToken(request, "accessToken");

        // accessToken이 존재하는 경우
        if (StringUtils.hasText(token)) {
            try {
                // 토큰을 통해 인증을 설정
                getAuthentication(token);

            } catch (ExpiredJwtException e) {
                // 토큰이 만료된 경우 refreshToken을 이용해 accessToken을 갱신
                String newAccessToken = tokenRenewalService.reissueAccessToken(response, getToken(request, "refreshToken"));

                // 갱신된 accessToken이 없는 경우
                if (!StringUtils.hasText(newAccessToken)) {
                    // accessToken 쿠키를 삭제
                    cookieHelper.deleteCookie(response, "accessToken");
                } else {
                    // 갱신된 accessToken으로 인증을 설정
                    getAuthentication(newAccessToken);
                }
            }
        }
        // 다음 필터를 호출
        filterChain.doFilter(request, response);
    }

    // 요청에서 토큰을 추출하는 메서드
    private String getToken(HttpServletRequest request, String token) {

        // 쿠키에서 토큰을 추출
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (token.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    // 토큰을 통해 인증을 설정하는 메서드
    private void getAuthentication(String token) {
        // 토큰을 파싱하여 클레임을 추출
        Claims claims = jwtTokenizer.parseAccessToken(token);

        String email = claims.getSubject();
        Long userId = claims.get("userId", Long.class);
        String name = claims.get("name", String.class);
        String username = claims.get("username", String.class);

        List<GrantedAuthority> authorities = getGrantedAuthorities(claims);
        Long blogId = claims.get("blogId", Long.class);

        // 사용자 정보를 CustomUserDetails 객체에 설정
        CustomUserDetails userDetails = new CustomUserDetails(userId, username, "", name, authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()), blogId);

        // 인증 객체를 생성하고 SecurityContext에 설정
        Authentication authentication = new JwtAuthenticationToken(authorities, userDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 클레임에서 권한 목록을 추출하는 메서드
    private List<GrantedAuthority> getGrantedAuthorities(Claims claims) {
        List<String> roles = (List<String>) claims.get("roles");
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(() -> role);
        }
        return authorities;
    }
}