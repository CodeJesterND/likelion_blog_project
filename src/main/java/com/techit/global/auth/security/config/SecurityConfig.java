package com.techit.global.auth.security.config;

import com.techit.global.auth.jwt.util.CookieHelper;
import com.techit.global.auth.security.filter.JwtAuthenticationFilter;
import com.techit.global.auth.jwt.service.TokenRenewalService;
import com.techit.global.auth.jwt.util.JwtTokenizer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenizer jwtTokenizer;
    private final TokenRenewalService tokenRenewalService;
    private final CookieHelper cookieHelper;

    // SecurityFilterChain 빈을 정의하여 시큐리티 필터 체인을 설정
    @Bean
    public SecurityFilterChain securityLoginFilterChain(HttpSecurity http) throws Exception{
        http.authorizeHttpRequests(request -> request
                        // 특정 요청 경로에 대해 인증을 허용하지 않음
                        .requestMatchers("/", "/users/register", "/login", "/logout", "/login-form").permitAll()
                        .requestMatchers("/blog/@**").permitAll()
                        .requestMatchers("/css/**").permitAll()
                        // 나머지 모든 요청은 인증이 필요함
                        .anyRequest()
                        .authenticated())

                // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenizer, tokenRenewalService, cookieHelper), UsernamePasswordAuthenticationFilter.class)

                // 기본 폼 로그인 비활성화
                .formLogin(formLogin -> formLogin.disable())
                // 로그아웃 비활성화
                .logout(logout -> logout.disable())
                // CSRF 보호 비활성화
                .csrf(csrf -> csrf.disable())
                // HTTP 기본 인증 비활성화
                .httpBasic(httpBasic -> httpBasic.disable())
                // CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // 필터 체인 빌드
        return http.build();
    }

    // CORS 설정을 정의하는 빈
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        // CORS 설정 소스 생성
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // CORS 구성 생성
        CorsConfiguration config = new CorsConfiguration();
        // 모든 출처 허용
        config.addAllowedOrigin("*");
        // 모든 헤더 허용
        config.addAllowedHeader("*");
        // 모든 HTTP 메서드 허용
        config.addAllowedMethod("*");
        config.setAllowedMethods(List.of("GET", "POST", "DELETE", "PUT", "PATCH", "OPTION"));

        // 모든 경로에 대해 CORS 구성 등록
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    // 비밀번호 인코더 빈 정의
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
