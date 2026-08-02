// 로그인 요청 데이터 (아이디/비번)
package com.example.ppe_server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String userId;
    private String password;
}