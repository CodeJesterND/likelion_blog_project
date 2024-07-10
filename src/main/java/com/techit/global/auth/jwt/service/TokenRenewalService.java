package com.techit.global.auth.jwt.service;

import jakarta.servlet.http.HttpServletResponse;

public interface TokenRenewalService {
    public String reissueAccessToken(HttpServletResponse response, String refreshToken);
}
