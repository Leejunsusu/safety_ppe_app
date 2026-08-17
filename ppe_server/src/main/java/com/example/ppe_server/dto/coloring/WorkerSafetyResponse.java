package com.example.ppe_server.dto.coloring;

import com.example.ppe_server.entity.coloring.SafetyGrade;
import lombok.Builder;
import lombok.Getter;

/**
 * 노동자 1명의 안전 상태 응답 (관리자 대시보드 목록의 각 항목)
 *
 * [출처] 명세서 FR-021 관리자 대시보드
 *   - 노동자별 컬러링 등급(GREEN/YELLOW/RED) 표시
 *   - RED 등급은 목록 최상단 우선 표시
 */
@Getter
@Builder
public class WorkerSafetyResponse {
    private String userId;          // 노동자 아이디
    private String name;            // 노동자 이름
    private String nickname;        // 노동자 닉네임
    private int violationCount;     // 미착용 누적 건수
    private SafetyGrade grade;      // 안전 등급 (컬러링)
}