package com.example.ppe_server.dto.alarm;

import lombok.Getter;

/**
 * FCM 토큰 저장 요청 (앱이 자기 FCM 토큰을 서버에 전송)
 */
@Getter
public class FcmTokenRequest {
    private String fcmToken;
}