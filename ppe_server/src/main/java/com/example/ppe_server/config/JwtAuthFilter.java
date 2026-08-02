package com.example.ppe_server.config;

// 요청마다 토큰을 검사하는 문지기(필터)

import com.example.ppe_server.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Authorization 헤더에서 토큰 꺼내기
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);   // "Bearer " 뒤의 토큰

            try {
                // 2. 토큰 검증 + user_id 꺼내기
                String userId = jwtUtil.getUserId(token);

                // 3. 인증 정보 등록 (이 사용자가 로그인됨을 표시)
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userId, null, List.of(new SimpleGrantedAuthority("USER")));
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                // 토큰이 유효하지 않으면 인증 없이 통과 (뒤에서 차단됨)
            }
        }

        // 4. 다음 단계로 진행
        filterChain.doFilter(request, response);
    }
}