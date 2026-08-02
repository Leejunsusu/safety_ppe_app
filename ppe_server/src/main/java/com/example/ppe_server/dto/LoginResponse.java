// 로그인 응답 데이터 (토큰/아이디/권한)
package com.example.ppe_server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String userId;
    private String role;
}