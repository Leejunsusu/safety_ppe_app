package com.example.ppe_server.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 사용자별 안전 등급(컬러링) 엔티티
 *
 * [출처] 명세서 FR-021/FR-022 대시보드
 *   - 노동자 한 명당 1행 (user_id가 PK, users 테이블과 1:1 관계)
 *   - 미착용(위반)이 누적될 때마다 violation_count를 올리고 grade를 다시 계산한다.
 *   - grade 기준: GREEN(0건) / YELLOW(1~2건) / RED(3건 이상)
 *
 * [DB 매핑] user_safety_status 테이블
 *
 * [연동 안내 - 감지 파트]
 *   감지 파트에서 미착용을 확정해 detection_records에 저장할 때,
 *   이 엔티티의 violation_count를 1 올리고 recalcGrade()를 호출해
 *   grade를 갱신한 뒤 저장하면 대시보드 등급이 자동으로 반영된다.
 *   (아래 increaseViolation() 메서드를 쓰면 두 작업이 한 번에 처리됨)
 */
@Entity
@Table(name = "user_safety_status")
@Getter
@NoArgsConstructor              // 기본 생성자
@AllArgsConstructor             // 빌더용 전체 생성자
@Builder                        // UserSafetyStatus.builder() 사용
public class UserSafetyStatus {

    @Id
    @Column(name = "user_id", length = 64)
    private String userId;                  // 노동자 아이디 (PK, User.userId와 1:1)

    @Column(name = "violation_count", nullable = false)
    private int violationCount;             // 미착용(위반) 누적 건수

    @Enumerated(EnumType.STRING) // grade를 문자열 GREEN/YELLOW/RED로 저장
    @Column(nullable = false, length = 10)
    private SafetyGrade grade;              // 현재 안전 등급 (컬러링)

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;        // 마지막 갱신 시각

    @PrePersist // 저장 직전(최초 생성 시) 자동 처리
    protected void onCreate() {
        this.updatedAt = LocalDateTime.now();
        // 최초 생성 시 등급이 비어 있으면 GREEN으로 기본 설정
        if (this.grade == null) {
            this.grade = SafetyGrade.GREEN;
        }
    }

    @PreUpdate // 수정 직전 갱신 시각 자동 업데이트
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 미착용(위반) 1건 추가.
     * violation_count를 1 올리고, 그에 맞춰 grade를 다시 계산한다.
     *
     * [사용처] 감지 파트에서 미착용 확정 시 호출하면 됨.
     */
    public void increaseViolation() {
        this.violationCount += 1;
        this.grade = SafetyGrade.fromViolationCount(this.violationCount);
    }

    /**
     * 위반 건수를 특정 값으로 직접 세팅하고 등급을 다시 계산한다.
     * (배치 재집계 등 필요 시 사용)
     */
    public void updateViolationCount(int newCount) {
        this.violationCount = newCount;
        this.grade = SafetyGrade.fromViolationCount(newCount);
    }

    /**
     * 특정 노동자의 안전상태를 GREEN(0건)으로 새로 만들 때 쓰는 팩토리 메서드.
     * 회원가입 직후나, 아직 안전상태 행이 없는 노동자를 조회할 때 기본값으로 사용.
     */
    public static UserSafetyStatus createDefault(String userId) {
        return UserSafetyStatus.builder()
                .userId(userId)
                .violationCount(0)
                .grade(SafetyGrade.GREEN)
                .build();
    }
}
