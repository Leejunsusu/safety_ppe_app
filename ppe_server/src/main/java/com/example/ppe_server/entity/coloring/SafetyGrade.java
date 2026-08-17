package com.example.ppe_server.entity.coloring;

/**
 * 안전 등급(컬러링 등급) enum
 *
 * [출처] 명세서 FR-021 관리자 대시보드
 *   - 노동자별 PPE 미착용 누적 건수(violation_count)에 따라 색상으로 구분
 *   - 등급 기준:
 *       GREEN  : 미착용 0건
 *       YELLOW : 미착용 1~2건
 *       RED    : 미착용 3건 이상
 *   - RED 등급 노동자는 대시보드 목록 최상단에 우선 표시된다.
 *
 * [DB 매핑] user_safety_status.grade 컬럼 ENUM('GREEN','YELLOW','RED')과 1:1 대응
 */
public enum SafetyGrade {
    GREEN,      // 안전 (미착용 0건)
    YELLOW,     // 주의 (미착용 1~2건)
    RED;        // 경고 (미착용 3건 이상)

    /**
     * 미착용 누적 건수를 받아서 해당하는 등급을 계산해 돌려주는 헬퍼 메서드.
     *
     * 감지 파트에서 미착용이 새로 기록될 때, violation_count를 1 올린 뒤
     * 이 메서드로 등급을 다시 계산해서 user_safety_status.grade를 갱신하면 된다.
     *
     * @param violationCount 미착용 누적 건수
     * @return 계산된 안전 등급
     */
    public static SafetyGrade fromViolationCount(int violationCount) {
        if (violationCount <= 0) {
            return GREEN;
        } else if (violationCount <= 2) {   // 1~2건
            return YELLOW;
        } else {                            // 3건 이상
            return RED;
        }
    }
}