package com.techit.global.auth.jwt.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtTokenizer {

    private final byte[] accessSecret;
    private final byte[] refreshSecret;

    // 접근 토큰 만료 시간 설정 (30분)
    public static Long ACCESS_TOKEN_EXPIRE_COUNT = 30 * 60 * 1000L;
    // 리프레시 토큰 만료 시간 설정 (7일)
    public static Long REFRESH_TOKEN_EXPIRE_COUNT = 7 * 24 * 60 * 60 * 1000L;

    // 주입된 시크릿 키 값을 UTF-8 바이트 배열로 변환
    public JwtTokenizer(@Value("${jwt.secretKey}") String accessSecret,
                        @Value("${jwt.refreshKey}") String refreshSecret) {

        this.accessSecret = accessSecret.getBytes(StandardCharsets.UTF_8);
        this.refreshSecret = refreshSecret.getBytes(StandardCharsets.UTF_8);
    }

    // 서명 키를 생성하는 정적 메서드
    public static Key getSigningKey(byte[] secretKey) {
        return Keys.hmacShaKeyFor(secretKey);
    }

    // ACCESS Token 생성 메서드
    public String createAccessToken(Long id, String email, String name, String username,
                                    List<String> roles, Long blogId) {

        Claims claims = Jwts.claims().setSubject(email);

        // 클레임에 사용자 정보 추가
        claims.put("userId", id);
        claims.put("username", username);
        claims.put("name", name);
        claims.put("roles", roles);
        claims.put("blogId", blogId);

        // 토큰 생성 및 서명
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + ACCESS_TOKEN_EXPIRE_COUNT))
                .signWith(getSigningKey(accessSecret))
                .compact();
    }

    // Refresh Token 생성 메서드
    public String createRefreshToken(Long id, String email) {

        Claims claims = Jwts.claims().setSubject(email);

        // 클레임에 사용자 ID 추가
        claims.put("userId", id);

        // 토큰 생성 및 서명
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + REFRESH_TOKEN_EXPIRE_COUNT))
                .signWith(getSigningKey(refreshSecret))
                .compact();
    }

    // 토큰 파싱 메서드
    public Claims parseToken(String token, byte[] secretKey){
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(secretKey))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            System.out.println("Token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("Unsupported JWT: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Malformed JWT: " + e.getMessage());
        } catch (SignatureException e) {
            System.out.println("Invalid signature: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Token is empty or null: " + e.getMessage());
        }
        return null;
    }

    // 액세스 토큰 파싱 메서드
    public Claims parseAccessToken(String accessToken) {
        return parseToken(accessToken, accessSecret);
    }

    // 리프레시 토큰 파싱 메서드
    public Claims parseRefreshToken(String refreshToken) {
        return parseToken(refreshToken, refreshSecret);
    }

    // 주어진 JWT 토큰에서 사용자 ID를 추출하는 메서드
//    public Long getUserIdFromToken(String token){
//        String[] tokenArr = token.split(" ");
//        token = tokenArr[1];
//        Claims claims = parseToken(token, accessSecret);
//        return Long.valueOf((Integer)claims.get("userId"));
//    }

    // 토큰 만료 여부 확인 메서드
    public boolean isTokenExpired(String token, byte[] secretKey) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(secretKey))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    // 리프레시 토큰 만료 여부 확인 메서드
    public boolean isRefreshTokenExpired(String token) {
        return isTokenExpired(token, refreshSecret);
    }
}