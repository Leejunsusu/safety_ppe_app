package com.example.ppe_server.dto.qr;

import lombok.Getter;
import lombok.NoArgsConstructor;

// QR 발급 요청 (POST /api/qr/issue)
// 노동자 본인의 QR을 발급/재발급한다. 누가 발급하는지는 로그인 토큰에서 확인.
@Getter
@NoArgsConstructor
public class GenerateQrRequest {
    // 유효 기간(일). null이면 서비스 기본값 사용 (예: 하루/1주/한달)
    private Integer validDays;
}