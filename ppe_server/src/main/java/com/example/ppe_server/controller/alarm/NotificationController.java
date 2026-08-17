package com.example.ppe_server.controller.alarm;

import com.example.ppe_server.dto.alarm.NotificationResponse;
import com.example.ppe_server.dto.alarm.NotificationSettingRequest;
import com.example.ppe_server.dto.alarm.NotificationSettingResponse;
import com.example.ppe_server.service.alarm.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 알림 관련 API
 *
 * [엔드포인트]
 *   GET    /api/notifications/settings   내 알림 설정 조회        (FR-024)
 *   PUT    /api/notifications/settings   내 알림 설정 변경(ON/OFF) (FR-024)
 *   GET    /api/notifications            내 알림 이력 조회         (FR-023)
 *   GET    /api/notifications/unread-count  안 읽은 알림 개수
 *   PUT    /api/notifications/{id}/read  특정 알림 읽음 처리
 *
 * ※ 모두 로그인 필요. @AuthenticationPrincipal로 로그인한 userId를 받아
 *   "본인 것만" 처리하므로 타인 데이터 접근이 원천 차단됨.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 내 알림 설정 조회
    @GetMapping("/settings")
    public ResponseEntity<NotificationSettingResponse> getSetting(
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(notificationService.getSetting(userId));
    }

    // 내 알림 설정 변경 (ON/OFF 토글)
    @PutMapping("/settings")
    public ResponseEntity<NotificationSettingResponse> updateSetting(
            @AuthenticationPrincipal String userId,
            @RequestBody NotificationSettingRequest request) {
        return ResponseEntity.ok(notificationService.updateSetting(userId, request.isEnabled()));
    }

    // 내 알림 이력 조회 (최신순)
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(notificationService.getMyNotifications(userId));
    }

    // 안 읽은 알림 개수 (뱃지 표시용)
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            @AuthenticationPrincipal String userId) {
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    // 특정 알림 읽음 처리
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<String> markAsRead(
            @AuthenticationPrincipal String userId,
            @PathVariable Long notificationId) {
        notificationService.markAsRead(userId, notificationId);
        return ResponseEntity.ok("읽음 처리되었습니다.");
    }
}