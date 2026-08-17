package com.example.ppe_server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 알림(FCM 발송 이력) 엔티티
 *
 * [출처] 명세서 FR-023 알림 송수신
 *   - PPE 미착용 감지 시, QR 만료 1시간 전 등 알림을 보낼 때마다 이 테이블에 1행 저장.
 *   - "알림을 끈 경우에도 앱 내 알림 이력에는 기록이 남는다"(FR-024) → 발송 여부와 별개로 이력은 항상 저장.
 *
 * [DB 매핑] notifications 테이블
 *
 * [연동 안내]
 *   - recordId: 어떤 감지 기록(detection_records) 때문에 발생한 알림인지 연결.
 *     감지와 무관한 알림(예: QR 만료 갱신 알림)은 recordId = null.
 *     실제 감지 기록 저장은 감지 파트에서 하므로, 여기서는 record_id 값(Long)만 느슨하게 보관한다.
 *   - 실제 FCM 푸시 발송(Firebase Admin SDK) 로직은 아직 미구현.
 *     지금은 "알림 이력을 DB에 남기고 조회하는" 부분까지만 뼈대로 만든 상태.
 *     추후 FcmService(가칭)를 만들어 발송 후 이 엔티티에 이력을 저장하도록 연결하면 된다.
 */
@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor              // 기본 생성자
@AllArgsConstructor             // 빌더용 전체 생성자
@Builder                        // Notification.builder() 사용
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    @Column(name = "notification_id")
    private Long notificationId;            // 알림 PK

    @Column(name = "record_id")
    private Long recordId;                  // 원인이 된 감지 기록 ID (없으면 null, 예: QR 만료 알림)

    @Column(name = "receiver_user_id", nullable = false, length = 64)
    private String receiverUserId;          // 알림을 받는 사람 (User.userId)

    @Column(nullable = false, length = 255)
    private String message;                 // 알림 내용 (예: "안전모 미착용이 감지되었습니다")

    // 알림 채널. 현재는 FCM만 존재하지만, 추후 확장 가능하도록 문자열로 저장.
    @Column(nullable = false, length = 20)
    private String channel;                 // 'FCM' (기본값)

    @Column(name = "sent_at", updatable = false)
    private LocalDateTime sentAt;           // 발송 시각

    @Column(name = "is_read", nullable = false)
    private boolean isRead;                 // 사용자가 읽었는지 여부 (기본 false)

    @PrePersist // 저장 직전 기본값 자동 설정
    protected void onCreate() {
        this.sentAt = LocalDateTime.now();
        if (this.channel == null) {
            this.channel = "FCM";
        }
    }

    /**
     * 알림을 '읽음' 상태로 변경.
     * [사용처] 사용자가 알림을 확인(클릭)했을 때 호출.
     */
    public void markAsRead() {
        this.isRead = true;
    }
}
