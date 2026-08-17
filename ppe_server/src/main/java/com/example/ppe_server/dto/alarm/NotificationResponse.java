package com.example.ppe_server.dto.alarm;

import com.example.ppe_server.entity.alarm.Notification;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 알림 1건 응답 (알림 이력 목록의 각 항목)
 *
 * [출처] 명세서 FR-023 알림 송수신 / FR-022 노동자 대시보드(경고 알림 이력)
 */
@Getter
@Builder
public class NotificationResponse {
    private Long notificationId;
    private Long recordId;          // 원인 감지 기록 ID (없으면 null)
    private String message;         // 알림 내용
    private LocalDateTime sentAt;   // 발송 시각
    private boolean isRead;         // 읽음 여부

    // Notification 엔티티 → 응답 DTO 변환
    public static NotificationResponse from(Notification n) {
        return NotificationResponse.builder()
                .notificationId(n.getNotificationId())
                .recordId(n.getRecordId())
                .message(n.getMessage())
                .sentAt(n.getSentAt())
                .isRead(n.isRead())
                .build();
    }
}