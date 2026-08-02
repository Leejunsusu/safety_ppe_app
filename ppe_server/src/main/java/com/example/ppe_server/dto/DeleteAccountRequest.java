// 회원 탈퇴 요청 (본인 확인용 비밀번호)
package com.example.ppe_server.dto;

import lombok.Getter; import lombok.Setter;

@Getter @Setter
public class DeleteAccountRequest {
    private String password;   // 본인 확인용 현재 비밀번호
}