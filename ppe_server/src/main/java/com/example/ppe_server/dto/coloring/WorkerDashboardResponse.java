package com.example.ppe_server.dto.coloring;

import com.example.ppe_server.dto.alarm.NotificationResponse;
import com.example.ppe_server.dto.detectionlog.DetectionRecordResponse;
import com.example.ppe_server.entity.coloring.SafetyGrade;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 노동자 대시보드 응답 (GET /api/dashboard/worker)
 *
 * [출처] 명세서 FR-022 노동자 대시보드
 *   - 본인 컬러링 등급
 *   - 본인 PPE 미착용 이력 (감지 일시/작업 종류/미착용 항목)
 *   - 본인이 받은 경고 알림 이력
 *   - 본인 데이터만 조회 가능 (다른 노동자 접근 차단)
 */
@Getter
@Builder
public class WorkerDashboardResponse {
    private String userId;                              // 본인 아이디
    private String name;                                // 본인 이름
    private int violationCount;                         // 미착용 누적 건수
    private SafetyGrade grade;                          // 본인 안전 등급 (컬러링)

    private List<DetectionRecordResponse> history;      // 본인 미착용 이력
    private List<NotificationResponse> notifications;   // 본인이 받은 알림 이력
}