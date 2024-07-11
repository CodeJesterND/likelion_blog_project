package com.techit.domains.user.controller;

import com.techit.domains.user.dto.LoginDto;
import com.techit.domains.user.entity.User;
import com.techit.domains.user.service.LoginService;
import com.techit.domains.user.service.UserService;
import com.techit.global.auth.jwt.service.RefreshTokenService;
import com.techit.global.auth.jwt.util.CookieHelper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;
    private final LoginService loginService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final CookieHelper cookieHelper;

    // 로그인 폼 요청 처리
    @GetMapping("/login-form")
    public String loginForm(Model model) {
        // 로그인 DTO 객체를 모델에 추가하여 뷰에서 사용할 수 있도록 함
        model.addAttribute("loginDto", new LoginDto());
        return "login/login-form";
    }

    // 로그인 요청 처리
    @PostMapping("/login")
    public String login(LoginDto loginDto,
                        BindingResult bindingResult,
                        HttpServletResponse response) {

        // 사용자 이름으로 사용자를 찾음
        User user = userService.findUserByUsername(loginDto.getUsername());

        // 사용자가 없거나 비밀번호가 일치하지 않는 경우 에러 메시지를 추가하고 로그인 폼으로 리턴
        if (user == null || !passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            bindingResult.rejectValue("password", null, "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "login/login-form";
        }

        // 로그인 서비스에서 액세스 토큰과 리프레시 토큰을 생성
        List<String> tokens = loginService.login(user);
        String accessToken = tokens.get(0);
        String refreshToken = tokens.get(1);

        // 액세스 토큰 쿠키 생성 및 설정
        cookieHelper.addCookie(response, "accessToken", accessToken);

        // 리프레시 토큰 쿠키 생성 및 설정
        cookieHelper.addCookie(response, "refreshToken", refreshToken);

        // 로그인 후 홈 페이지로 리다이렉트
        return "redirect:/";
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response) {

        // 요청에서 모든 쿠키를 가져옴
        Cookie[] cookies = request.getCookies();

        // 리프레시 토큰 쿠키를 찾고, 해당 토큰을 삭제
        for (Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) {
                if (refreshTokenService.findRefreshToken(cookie.getValue()).isPresent()) {
                    refreshTokenService.deleteRefreshToken(cookie.getValue());
                }
            }
        }

        // 액세스 토큰 쿠키 삭제
        cookieHelper.deleteCookie(response, "accessToken");

        // 리프레시 토큰 쿠키 삭제
        cookieHelper.deleteCookie(response, "refreshToken");

        // SecurityContext 초기화
        SecurityContextHolder.clearContext();

        // 로그아웃 후 홈페이지로 리다이렉트
        return "redirect:/";
    }
}