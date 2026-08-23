package com.example.ppe_server.dto.detectionlog;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 앱(온디바이스 tflite 감지)이 서버로 보내는 감지 결과 요청 DTO
 *
 * 앱에서 PPE 미착용이 확정되면 이 형식의 JSON을 서버로 전송한다.
 * 서버는 이걸 받아서 detection_records + detection_ppe_details 에 저장한다.
 */
@Getter
@Setter
@NoArgsConstructor
public class DetectionSaveRequest {

    private String clientUuid;       // 앱이 만든 고유 UUID (중복 저장 방지용)
    private String targetUserId;     // 미착용이 감지된 노동자 아이디
    private String inspectorUserId;  // 순찰(INSPECTION) 모드 시 점검한 관리자, 게이트면 null
    private Integer siteId;          // 감지가 발생한 현장 ID
    private String detectionMode;    // "GATE" 또는 "INSPECTION"
    private String workType;         // 순찰 모드 작업종류, 게이트면 null
    private String s3Key;            // 캡처 사진 S3 경로 (없으면 null)

    // 감지된 PPE 항목들 (착용/미착용 여러 개)
    private List<PpeItemResult> items;

    /**
     * 개별 PPE 항목 결과
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class PpeItemResult {
        private Integer ppeId;         // PPE 항목 ID (ppe_items.ppe_id)
        private Boolean worn;          // 착용 여부 (true=착용, false=미착용)
        private BigDecimal confidence; // 감지 신뢰도
    }
}