// 비밀번호 재설정 요청 (인증된 이메일 + 새 비밀번호)
package com.example.ppe_server.dto;

import lombok.Getter; import lombok.Setter;

@Getter @Setter
public class ResetPasswordRequest {
    private String email;
    private String newPassword;
}