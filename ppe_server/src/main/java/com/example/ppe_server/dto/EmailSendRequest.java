// 이메일 인증코드 발송 요청 (이메일, 용도)
package com.example.ppe_server.dto;

import com.example.ppe_server.entity.Purpose;
import lombok.Getter; import lombok.Setter;

@Getter @Setter
public class EmailSendRequest {
    private String email;
    private Purpose purpose;   // SIGNUP / FIND_ID / RESET_PW
}