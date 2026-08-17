package com.example.ppe_server.dto.qr;

import lombok.Builder;
import lombok.Getter;

// QR 검증 결과 응답 (게이트에서 노동자 신원 확인 + 유효성 판단)
@Getter
@Builder
public class VerifyQrResponse {
    private boolean valid;          // 검증 성공 여부
    private String userId;          // 유효할 경우 이 QR의 주인(노동자 아이디)
    private String name;            // 노동자 실명
    private Integer siteId;         // 노동자 소속 현장 (없으면 null)
    private String message;         // 결과 메시지
}