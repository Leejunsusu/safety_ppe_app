package com.example.ppe_server.config;

// 보안 설정 (경로별 인증 허용/차단, JWT 필터 연결)

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 세션 안 씀(토큰 방식)
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 허용할 경로
                        .requestMatchers("/api/users/signup", "/api/users/login",
                                "/api/users/find-id", "/api/users/reset-password",
                                "/api/email/send", "/api/email/verify").permitAll()
                        // 나머지는 토큰 필요
                        .anyRequest().authenticated()
                )
                // JWT 필터를 기본 인증 필터 앞에 끼워넣기
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}