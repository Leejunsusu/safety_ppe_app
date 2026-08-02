// 이메일 인증코드 검증 요청 (이메일, 코드, 용도)
package com.example.ppe_server.dto;

import com.example.ppe_server.entity.Purpose;
import lombok.Getter; import lombok.Setter;

@Getter @Setter
public class EmailVerifyRequest {
    private String email;
    private String code;
    private Purpose purpose;
}