package com.example.ppe_server.service;

import com.example.ppe_server.dto.NotificationResponse;
import com.example.ppe_server.dto.NotificationSettingResponse;
import com.example.ppe_server.entity.Notification;
import com.example.ppe_server.entity.User;
import com.example.ppe_server.repository.NotificationRepository;
import com.example.ppe_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 알림 서비스
 *
 * [담당 기능]
 *   - FR-024 알림 설정: 알림 ON/OFF 조회/변경
 *   - FR-023 알림 이력: 내 알림 목록 조회, 읽음 처리, 안 읽은 개수
 *
 * [미구현 - 추후]
 *   - 실제 FCM 푸시 발송(Firebase Admin SDK)은 여기에 없음.
 *     감지 파트에서 미착용 확정 시, FcmService(가칭)를 만들어
 *     (1) FCM 발송 (2) Notification 이력 저장 을 하도록 연결하면 됨.
 *     이력 저장용으로 아래 createNotification()을 참고/재사용할 수 있다.
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository; // 친구가 만든 UserRepository 사용

    // ===================== FR-024 알림 설정 =====================

    /**
     * 현재 알림 설정 조회.
     * User.notificationEnabled 값을 그대로 반환.
     */
    @Transactional(readOnly = true)
    public NotificationSettingResponse getSetting(String userId) {
        User user = getUser(userId);
        return NotificationSettingResponse.builder()
                .userId(user.getUserId())
                .enabled(user.isNotificationEnabled())
                .build();
    }

    /**
     * 알림 설정 변경 (ON/OFF).
     *
     * ★ User 엔티티에 아래 메서드가 있어야 함 (없으면 추가 필요):
     *
     *     public void updateNotificationEnabled(boolean enabled) {
     *         this.notificationEnabled = enabled;
     *     }
     *
     *   (전에 assignSite()를 추가했던 것과 같은 방식. User.java의 updatePassword 근처에 추가)
     */
    @Transactional
    public NotificationSettingResponse updateSetting(String userId, boolean enabled) {
        User user = getUser(userId);
        user.updateNotificationEnabled(enabled);   // ← User에 이 메서드 추가 필요
        userRepository.save(user);
        return NotificationSettingResponse.builder()
                .userId(user.getUserId())
                .enabled(enabled)
                .build();
    }

    // ===================== FR-023 알림 이력 =====================

    /**
     * 내 알림 이력 조회 (최신순).
     * 명세서: "알림을 끈 경우에도 앱 내 알림 이력에는 기록이 남는다"
     * → 설정과 무관하게 저장된 모든 알림을 보여준다.
     */
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(String userId) {
        return notificationRepository.findByReceiverUserIdOrderBySentAtDesc(userId)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }

    /**
     * 안 읽은 알림 개수 (알림 뱃지 표시용).
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(String userId) {
        return notificationRepository.countByReceiverUserIdAndIsReadFalse(userId);
    }

    /**
     * 특정 알림을 '읽음'으로 변경.
     * 본인이 받은 알림만 처리 가능하도록 소유자 검증.
     */
    @Transactional
    public void markAsRead(String userId, Long notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));

        // 보안: 남의 알림을 읽음 처리할 수 없도록 소유자 확인
        if (!n.getReceiverUserId().equals(userId)) {
            throw new IllegalStateException("본인의 알림만 처리할 수 있습니다.");
        }

        n.markAsRead();
        notificationRepository.save(n);
    }

    // ===================== 알림 이력 생성(저장) =====================

    /**
     * 알림 이력 1건 저장.
     *
     * [사용처 - 추후]
     *   감지 파트에서 미착용 감지 후 FCM 발송할 때, 발송 이력을 남기려고 이 메서드를 호출.
     *   또는 QR 만료 갱신 알림을 보낼 때도 사용 가능.
     *
     * @param receiverUserId 알림 받는 사람
     * @param message        알림 내용
     * @param recordId       원인 감지 기록 ID (없으면 null)
     */
    @Transactional
    public Notification createNotification(String receiverUserId, String message, Long recordId) {
        Notification n = Notification.builder()
                .receiverUserId(receiverUserId)
                .message(message)
                .recordId(recordId)
                .channel("FCM")
                .isRead(false)
                .build();
        return notificationRepository.save(n);
    }

    // ===================== 내부 헬퍼 =====================

    private User getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }
}
