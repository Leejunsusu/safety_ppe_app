package com.example.ppe_server.entity.detectionlog;

/**
 * 감지 모드 enum
 *
 * [출처] 명세서 FR-015~020, 감지(YOLO) 파트
 *   - GATE       : 공통 게이트 모드. 출입 게이트에서 개인보호구 착용 여부를 감지.
 *   - INSPECTION : 작업 상황(순찰) 모드. 안전관리자가 특정 노동자를 지목해 점검.
 *
 * [DB 매핑] detection_records.detection_mode 컬럼 ENUM('GATE','INSPECTION')과 1:1 대응
 *
 * ※ 이 enum은 감지 기록(DetectionRecord)에서 사용된다.
 *   실제 감지 로직(YOLO 결과 저장)은 감지 파트에서 구현 예정.
 */
public enum DetectionMode {
    GATE,           // 공통 게이트 모드 (출입 시 감지)
    INSPECTION      // 작업 상황(순찰) 모드 (관리자가 지목 점검)
}