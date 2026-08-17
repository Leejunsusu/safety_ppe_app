package com.example.ppe_server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

// QR 검증 요청 (POST /api/qr/verify)
// 게이트에서 스캔한 QR 토큰을 보내 유효성을 확인한다.
@Getter
@NoArgsConstructor
public class VerifyQrRequest {
    private String token;       // 스캔한 QR에서 읽어낸 토큰
}
