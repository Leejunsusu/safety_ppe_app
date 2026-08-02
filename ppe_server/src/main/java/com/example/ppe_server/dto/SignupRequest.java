// 회원가입 요청 데이터
package com.example.ppe_server.dto;

import com.example.ppe_server.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    private String userId;      // 아이디
    private String password;    // 비밀번호 (평문 - 서버에서 해시함)
    private String name;        // 이름
    private String nickname;    // 닉네임
    private String email;       // 이메일
    private Role role;          // ADMIN / WORKER
}