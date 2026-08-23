package com.example.ppe_server.service.detectionlog;

import com.example.ppe_server.dto.detectionlog.DetectionSaveRequest;
import com.example.ppe_server.entity.detectionlog.DetectionMode;
import com.example.ppe_server.entity.detectionlog.DetectionPpeDetail;
import com.example.ppe_server.entity.detectionlog.DetectionRecord;
import com.example.ppe_server.entity.alarm.Notification;
import com.example.ppe_server.entity.coloring.UserSafetyStatus;
import com.example.ppe_server.repository.detectionlog.DetectionPpeDetailRepository;
import com.example.ppe_server.repository.detectionlog.DetectionRecordRepository;
import com.example.ppe_server.repository.alarm.NotificationRepository;
import com.example.ppe_server.repository.coloring.UserSafetyStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 감지 결과 저장 서비스
 *
 * 앱(온디바이스 tflite)에서 미착용을 확정해 보낸 결과를 받아
 *   1) detection_records 저장 (감지 기록 1건)
 *   2) detection_ppe_details 저장 (미착용/착용 항목들)
 *   3) 미착용이 있으면 해당 노동자의 안전등급 갱신 (UserSafetyStatus)
 *   4) 미착용이 있으면 알림 이력 저장 (Notification) - FCM 실제 발송은 추후
 * 을 한 번에 처리한다.
 *
 * @Transactional : 아래 저장들이 전부 성공하거나 전부 취소되도록 묶는다.
 */
@Service
@RequiredArgsConstructor
public class DetectionService {

    private final DetectionRecordRepository detectionRecordRepository;
    private final DetectionPpeDetailRepository detectionPpeDetailRepository;
    private final UserSafetyStatusRepository userSafetyStatusRepository;
    private final NotificationRepository notificationRepository;

    // PPE id → 한글 이름 (알림 메시지에 쓰려고. 없으면 그냥 "PPE"로 표시)
    // 인계문서 팀장 클래스 순서 기준 (0~15)
    private static final String[] PPE_NAMES_KO = {
            "화학용 장갑", "방진 마스크", "안면 보호구", "각반", "방독 마스크",
            "안전모", "안전모(끈 미체결)", "보호복", "보안경", "안전장갑",
            "안전대", "안전고리", "안전화", "용접 각반", "용접 장갑", "용접 보호면"
    };

    /**
     * 감지 결과를 저장한다.
     *
     * @param request 앱이 보낸 감지 결과
     * @return 저장된 감지 기록의 recordId (이미 저장된 중복이면 기존 recordId)
     */
    @Transactional
    public Long saveDetection(DetectionSaveRequest request) {

        // ── 1) 중복 체크 (멱등성) ──────────────────────────────
        var existing = detectionRecordRepository.findByClientUuid(request.getClientUuid());
        if (existing.isPresent()) {
            return existing.get().getRecordId();
        }

        // ── 2) detection_records 저장 ─────────────────────────
        DetectionRecord record = DetectionRecord.builder()
                .clientUuid(request.getClientUuid())
                .targetUserId(request.getTargetUserId())
                .inspectorUserId(request.getInspectorUserId())
                .siteId(request.getSiteId())
                .detectionMode(DetectionMode.valueOf(request.getDetectionMode()))
                .workType(request.getWorkType())
                .s3Key(request.getS3Key())
                .build();

        DetectionRecord savedRecord = detectionRecordRepository.save(record);
        Long recordId = savedRecord.getRecordId();

        // ── 3) detection_ppe_details 저장 + 미착용 항목 수집 ────
        boolean hasNotWorn = false;                      // 미착용이 하나라도 있는지
        List<Integer> notWornPpeIds = new ArrayList<>(); // 미착용된 PPE id 목록 (알림 메시지용)

        List<DetectionSaveRequest.PpeItemResult> items = request.getItems();
        if (items != null) {
            for (DetectionSaveRequest.PpeItemResult item : items) {
                DetectionPpeDetail detail = DetectionPpeDetail.builder()
                        .recordId(recordId)
                        .ppeId(item.getPpeId())
                        .worn(item.getWorn())
                        .confidence(item.getConfidence())
                        .build();
                detectionPpeDetailRepository.save(detail);

                if (Boolean.FALSE.equals(item.getWorn())) {
                    hasNotWorn = true;
                    notWornPpeIds.add(item.getPpeId());
                }
            }
        }

        // ── 4) 미착용이 있을 때: 안전등급 갱신 + 알림 이력 저장 ──
        if (hasNotWorn) {
            String userId = request.getTargetUserId();

            // (4-1) 안전등급 갱신
            UserSafetyStatus status = userSafetyStatusRepository.findById(userId)
                    .orElseGet(() -> UserSafetyStatus.createDefault(userId));
            status.increaseViolation();
            userSafetyStatusRepository.save(status);

            // (4-2) 알림 이력 저장 (FCM 실제 발송은 추후, 지금은 DB 이력만)
            String message = buildNotWornMessage(notWornPpeIds);
            Notification notification = Notification.builder()
                    .recordId(recordId)
                    .receiverUserId(userId)
                    .message(message)
                    .channel("FCM")
                    .isRead(false)
                    .build();
            notificationRepository.save(notification);
        }

        return recordId;
    }

    /**
     * 미착용 PPE id 목록으로 알림 메시지를 만든다.
     * 예: "안전모, 안전장갑 미착용이 감지되었습니다."
     */
    private String buildNotWornMessage(List<Integer> notWornPpeIds) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < notWornPpeIds.size(); i++) {
            int id = notWornPpeIds.get(i);
            String name = (id >= 0 && id < PPE_NAMES_KO.length) ? PPE_NAMES_KO[id] : "PPE";
            sb.append(name);
            if (i < notWornPpeIds.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(" 미착용이 감지되었습니다.");
        return sb.toString();
    }
}