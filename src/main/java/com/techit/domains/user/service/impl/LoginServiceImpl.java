package com.techit.domains.user.service.impl;

import com.techit.domains.user.entity.User;
import com.techit.domains.user.service.LoginService;
import com.techit.global.auth.jwt.entity.RefreshToken;
import com.techit.global.auth.jwt.service.RefreshTokenService;
import com.techit.global.auth.jwt.util.JwtTokenizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;

    @Override
    public List<String> login(User user) {
        // 사용자로부터 역할을 추출
        List<String> roles = rolesExtract(user);

        // 액세스 토큰 생성
        String accessToken = accessTokenCreate(user, roles);

        // 리프레시 토큰 생성 및 저장
        String refreshToken = refreshTokenCreateAndSave(user);

        // 액세스 토큰과 리프레시 토큰을 리스트로 반환
        return List.of(accessToken, refreshToken);
    }

    // 사용자로부터 역할을 추출하는 메서드
    private List<String> rolesExtract(User user) {
        return user.getUserRoles()
                .stream()
                .map(role -> role.getRole().getRoleEnum().name())
                .toList();
    }

    // 사용자 정보를 기반으로 액세스 토큰을 생성하는 메서드
    private String accessTokenCreate(User user, List<String> roles) {
        return jwtTokenizer.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getUsername(),
                roles,
                user.getBlog().getId()
        );
    }

    // 리프레시 토큰을 생성하고 데이터베이스에 저장하는 메서드
    private String refreshTokenCreateAndSave(User user) {
        // 리프레시 토큰 생성
        String refreshToken = jwtTokenizer.createRefreshToken(user.getId(), user.getEmail());

        // 리프레시 토큰 엔티티 생성 및 설정
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setValue(refreshToken);
        refreshTokenEntity.setUserId(user.getId());

        // 리프레시 토큰을 데이터베이스에 저장
        refreshTokenService.addRefreshToken(refreshTokenEntity);

        // 생성된 리프레시 토큰 반환
        return refreshToken;
    }
}