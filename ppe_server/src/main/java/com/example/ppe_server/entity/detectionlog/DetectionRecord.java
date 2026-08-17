package com.example.ppe_server.entity.detectionlog;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 감지 기록(위반 이벤트) 엔티티
 *
 * [출처] 명세서 FR-016/FR-020 결과 처리, FR-021/FR-022 대시보드
 *   - PPE 미착용이 '확정'된 경우에만 1행 저장된다. (정상 통과는 저장하지 않음 = 위반만 기록)
 *   - 대시보드에서 "미착용 건수", "위반 이력"을 보여줄 때 이 테이블을 조회한다.
 *
 * [DB 매핑] detection_records 테이블
 *
 * ★★★ 중요 - 감지(YOLO) 파트 담당자에게 ★★★
 *   이 엔티티는 대시보드가 데이터를 "읽기" 위해 미리 만들어 둔 뼈대입니다.
 *   실제로 이 테이블에 감지 결과를 "저장"하는 로직(YOLO 감지 → 미착용 확정 → 저장)은
 *   감지 파트에서 구현해야 합니다.
 *   저장 시 아래도 함께 처리하면 대시보드가 자동으로 반영됩니다.
 *     1) detection_records에 이 엔티티로 1행 저장
 *     2) 미착용 항목이 있으면 detection_ppe_details에도 저장 (별도 엔티티 필요)
 *     3) 해당 노동자의 UserSafetyStatus.increaseViolation() 호출로 등급 갱신
 *     4) 필요 시 Notification 이력 저장 + FCM 발송
 *
 *   ※ 이 클래스에 새 필드/연관관계를 추가하고 싶으면 감지 파트와 먼저 상의해 주세요.
 *     (대시보드 조회 코드가 이 필드들을 참조하므로, 이름 변경 시 알려주시면 맞추겠습니다.)
 */
@Entity
@Table(name = "detection_records")
@Getter
@NoArgsConstructor              // 기본 생성자
@AllArgsConstructor             // 빌더용 전체 생성자
@Builder                        // DetectionRecord.builder() 사용
public class DetectionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    @Column(name = "record_id")
    private Long recordId;                  // 감지 기록 PK

    // 앱이 생성한 UUID. 오프라인 큐에서 같은 감지가 중복 전송되는 것을 막는 멱등성 키.
    @Column(name = "client_uuid", nullable = false, unique = true, length = 64)
    private String clientUuid;

    @Column(name = "target_user_id", nullable = false, length = 64)
    private String targetUserId;            // 미착용이 감지된 노동자 (User.userId)

    // 순찰(INSPECTION) 모드에서 점검을 수행한 안전관리자. 게이트 모드면 null.
    @Column(name = "inspector_user_id", length = 64)
    private String inspectorUserId;

    @Column(name = "site_id", nullable = false)
    private Integer siteId;                 // 감지가 발생한 현장 (Site.siteId)

    @Enumerated(EnumType.STRING) // GATE / INSPECTION 을 문자열로 저장
    @Column(name = "detection_mode", nullable = false, length = 20)
    private DetectionMode detectionMode;    // 감지 모드

    // 순찰 모드의 작업 종류(고소/분진/도장/용접 등). 게이트 모드면 null.
    @Column(name = "work_type", length = 20)
    private String workType;

    // 미착용 캡처 사진의 S3 저장 경로. (S3 연동은 시스템 관리자/감지 파트 담당)
    @Column(name = "s3_key", length = 512)
    private String s3Key;

    @Column(name = "detected_at", updatable = false)
    private LocalDateTime detectedAt;       // 감지 시각

    @PrePersist // 저장 직전 감지 시각 자동 설정
    protected void onCreate() {
        if (this.detectedAt == null) {
            this.detectedAt = LocalDateTime.now();
        }
    }
}