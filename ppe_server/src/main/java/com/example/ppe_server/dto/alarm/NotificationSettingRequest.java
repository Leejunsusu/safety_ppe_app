package com.example.ppe_server.dto.alarm;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 알림 설정 변경 요청 (PUT /api/notifications/settings)
 *
 * [출처] 명세서 FR-024 알림 설정
 *   - 토글 스위치로 알림 ON/OFF
 *
 * ※ 현재는 User.notificationEnabled 하나만 토글하는 단순 구조.
 *   명세서상 "역할별로 알림 종류가 다름"(관리자: 미착용 푸시, 노동자: 경고/QR갱신 알림)이지만,
 *   지금은 전체 알림 ON/OFF 하나로 뼈대만 구현. 세분화는 추후 확장.
 */
@Getter
@NoArgsConstructor
public class NotificationSettingRequest {
    private boolean enabled;    // true = 알림 받기, false = 알림 끄기
}