// 아이디 찾기 요청 (인증된 이메일)
package com.example.ppe_server.dto;

import lombok.Getter; import lombok.Setter;

@Getter @Setter
public class FindIdRequest {
    private String email;
}