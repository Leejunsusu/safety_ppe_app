package com.example.ppe_server.dto.detectionlog;

import com.example.ppe_server.entity.detectionlog.DetectionRecord;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 감지(미착용) 기록 1건 응답 (노동자 대시보드 - 미착용 이력의 각 항목)
 *
 * [출처] 명세서 FR-022 노동자 대시보드
 *   - 미착용 이력: 감지 일시, 작업 종류, (미착용 항목)
 *
 * [뼈대 안내]
 *   미착용 '항목'(예: 안전모, 안전화)은 detection_ppe_details 테이블에 있다.
 *   해당 테이블/엔티티 연동은 감지 파트와 함께 추후 추가. (여기선 감지 기록 기본 정보만)
 */
@Getter
@Builder
public class DetectionRecordResponse {
    private Long recordId;
    private LocalDateTime detectedAt;   // 감지 일시
    private String detectionMode;       // GATE / INSPECTION
    private String workType;            // 작업 종류 (게이트면 null)
    private Integer siteId;             // 발생 현장
    private String summary;              // "안전모 외 2건 미착용"
    private List<String> notWornItems;   // ["안전모", "안전대", "각반"]
    private String s3Key;                // 사진 경로

    // 미착용 장비 이름 목록을 받아서 변환
    public static DetectionRecordResponse from(DetectionRecord r, List<String> notWornItems) {
        return DetectionRecordResponse.builder()
                .recordId(r.getRecordId())
                .detectedAt(r.getDetectedAt())
                .detectionMode(r.getDetectionMode() != null ? r.getDetectionMode().name() : null)
                .workType(r.getWorkType())
                .siteId(r.getSiteId())
                .summary(buildSummary(notWornItems))
                .notWornItems(notWornItems)
                .s3Key(r.getS3Key())
                .build();
    }

    // "안전모 외 2건 미착용" 만들기
    private static String buildSummary(List<String> items) {
        if (items == null || items.isEmpty()) {
            return "미착용 항목 없음";
        }
        if (items.size() == 1) {
            return items.get(0) + " 미착용";
        }
        return items.get(0) + " 외 " + (items.size() - 1) + "건 미착용";
    }
}