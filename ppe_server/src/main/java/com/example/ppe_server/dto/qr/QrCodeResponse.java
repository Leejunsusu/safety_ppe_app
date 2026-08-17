package com.example.ppe_server.dto.qr;

import com.example.ppe_server.entity.qr.QrToken;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// QR 발급 결과 응답
@Getter
@Builder
public class QrCodeResponse {
    private String userId;
    private String token;
    private LocalDateTime issuedAt;
    private LocalDateTime expireAt;
    private String qrImageBase64;   // QR 이미지(PNG)를 Base64로 인코딩한 문자열

    // QrToken 엔티티 + 이미지 문자열을 응답 DTO로 변환
    public static QrCodeResponse of(QrToken qr, String qrImageBase64) {
        return QrCodeResponse.builder()
                .userId(qr.getUserId())
                .token(qr.getToken())
                .issuedAt(qr.getIssuedAt())
                .expireAt(qr.getExpireAt())
                .qrImageBase64(qrImageBase64)
                .build();
    }
}