package com.techit.global.auth.jwt.service.impl;

import com.techit.domains.user.entity.User;
import com.techit.domains.user.repository.UserRepository;
import com.techit.global.auth.jwt.service.RefreshTokenService;
import com.techit.global.auth.jwt.service.TokenRenewalService;
import com.techit.global.auth.jwt.util.CookieHelper;
import com.techit.global.auth.jwt.util.JwtTokenizer;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenRenewalServiceImpl implements TokenRenewalService {

    // 필요한 서비스와 리포지토리 주입
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenizer jwtTokenizer;
    private final UserRepository userRepository;
    private final CookieHelper cookieHelper;

    // accessToken이 만료되면 refreshToken으로 다시 accessToken을 재발급하는 메서드
    @Override
    public String reissueAccessToken(HttpServletResponse response, String refreshToken) {

        // refreshToken이 유효하지 않거나 만료된 경우
        if (jwtTokenizer.isRefreshTokenExpired(refreshToken)) {

            // refreshToken과 accessToken 쿠키를 삭제
            cookieHelper.deleteCookie(response, "refreshToken");

            // refreshToken이 데이터베이스에 존재하면 삭제
            if (refreshTokenService.findRefreshToken(refreshToken).isPresent()) {
                refreshTokenService.deleteRefreshToken(refreshToken);
            }

            return "";
        }

        // 유효한 refreshToken인 경우 클레임을 파싱
        Claims claims = jwtTokenizer.parseRefreshToken(refreshToken);

        // 클레임에서 사용자 ID를 추출하고, 사용자 정보를 조회
        User user = userRepository.findById(claims.get("userId", Long.class)).orElse(null);

        // 새로운 accessToken을 생성
        String accessToken = jwtTokenizer.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getUsername(),
                user.getUserRoles()
                        .stream().map(role -> role.getRole().getRoleEnum().name()).toList(),
                user.getBlog().getId()
        );

        // 새로운 accessToken을 쿠키에 설정
        cookieHelper.addCookie(response, "accessToken", accessToken);

        return accessToken;
    }
}