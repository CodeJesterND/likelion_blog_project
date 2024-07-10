package com.techit.global.auth.jwt.service.impl;

import com.techit.global.auth.jwt.entity.RefreshToken;
import com.techit.global.auth.jwt.repository.RefreshTokenRepository;
import com.techit.global.auth.jwt.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // 리프레시 토큰을 추가하는 메서드
    @Override
    public RefreshToken addRefreshToken(RefreshToken refreshToken) {

        // 리프레시 토큰을 저장소에 저장
        return refreshTokenRepository.save(refreshToken);
    }

    // 리프레시 토큰을 조회하는 메서드
    @Transactional(readOnly = true)
    @Override
    public Optional<RefreshToken> findRefreshToken(String refreshToken) {

        // 저장소에서 리프레시 토큰을 값으로 조회
        return refreshTokenRepository.findByValue(refreshToken);
    }

    // 리프레시 토큰을 삭제하는 메서드
    @Override
    public void deleteRefreshToken(String refreshToken) {

        // 저장소에서 리프레시 토큰을 값으로 조회하고, 존재하면 삭제
        refreshTokenRepository.findByValue(refreshToken).ifPresent(refreshTokenRepository::delete);
    }
}