package com.example.ppe_server.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 관리자 대시보드 응답 (GET /api/dashboard/manager)
 *
 * [출처] 명세서 FR-021 관리자 대시보드
 *   - 상단 요약: 오늘 총 입장 / 정상 통과 / 미착용 발생 건수
 *   - 하단 목록: 소속 현장 노동자별 안전 등급 (RED 우선 정렬)
 *
 * [뼈대 안내]
 *   - todayViolationCount(오늘 미착용 건수)는 detection_records 조회로 채운다.
 *   - todayEntryCount / todayPassCount(총 입장 / 정상 통과)는 감지 파트가
 *     "정상 통과"도 어딘가에 집계해줘야 정확히 채울 수 있다.
 *     현재 detection_records는 '위반만' 저장하므로 정상 통과 수를 알 수 없다.
 *     → 지금은 0으로 두고, 감지 파트와 집계 방식을 정한 뒤 채우기로 함. (TODO)
 */
@Getter
@Builder
public class ManagerDashboardResponse {
    private Integer siteId;                     // 관리자의 소속 현장
    private String siteName;                    // 현장 이름

    // === 상단 요약 통계 (오늘 기준) ===
    private long todayEntryCount;               // 오늘 총 입장 건수 (TODO: 감지 파트 집계 필요)
    private long todayPassCount;                // 오늘 정상 통과 건수 (TODO: 감지 파트 집계 필요)
    private long todayViolationCount;           // 오늘 미착용 발생 건수 (detection_records로 집계 가능)

    // === 하단: 노동자별 안전 등급 목록 (RED > YELLOW > GREEN 순) ===
    private List<WorkerSafetyResponse> workers;
}
