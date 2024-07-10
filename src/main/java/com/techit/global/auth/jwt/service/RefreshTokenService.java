package com.techit.global.auth.jwt.service;

import com.techit.global.auth.jwt.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    public RefreshToken addRefreshToken(RefreshToken refreshToken);
    public Optional<RefreshToken> findRefreshToken(String refreshToken);
    public void deleteRefreshToken(String refreshToken);
}