package com.example.ppe_server.dto.alarm;

import lombok.Builder;
import lombok.Getter;

/**
 * 알림 설정 조회/변경 결과 응답
 *
 * [출처] 명세서 FR-024 알림 설정
 */
@Getter
@Builder
public class NotificationSettingResponse {
    private String userId;          // 사용자 아이디
    private boolean enabled;        // 현재 알림 수신 여부
}