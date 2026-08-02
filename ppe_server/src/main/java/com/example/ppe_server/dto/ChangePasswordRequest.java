// 비밀번호 변경 요청 (현재 비밀번호 + 새 비밀번호)
package com.example.ppe_server.dto;

import lombok.Getter; import lombok.Setter;

@Getter @Setter
public class ChangePasswordRequest {
    private String currentPassword;   // 현재 비밀번호 (본인 확인용)
    private String newPassword;       // 새 비밀번호
}