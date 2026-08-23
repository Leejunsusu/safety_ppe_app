package com.example.ppe_server.repository.detectionlog;

import com.example.ppe_server.entity.detectionlog.DetectionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 감지 기록(detection_records) 리포지토리
 *
 * [사용처] 대시보드 통계/이력 조회 (DashboardService)
 *
 * ※ 저장(감지 결과 insert)은 감지 파트에서 담당.
 *   여기 정의된 메서드들은 대시보드가 '읽기' 위해 사용한다.
 */
public interface DetectionRecordRepository extends JpaRepository<DetectionRecord, Long> {

    // 특정 노동자의 미착용 이력 (노동자 대시보드) - 최신순
    List<DetectionRecord> findByTargetUserIdOrderByDetectedAtDesc(String targetUserId);

    // 특정 현장의 미착용 이력 (관리자 대시보드) - 최신순
    List<DetectionRecord> findBySiteIdOrderByDetectedAtDesc(Integer siteId);

    // 특정 현장에서 특정 기간 동안 발생한 미착용 건수 (관리자 대시보드 '오늘 미착용 건수' 등)
    long countBySiteIdAndDetectedAtBetween(Integer siteId, LocalDateTime from, LocalDateTime to);

    // 특정 노동자의 특정 기간 미착용 건수 (노동자 대시보드 통계)
    long countByTargetUserIdAndDetectedAtBetween(String targetUserId, LocalDateTime from, LocalDateTime to);

    // ── [감지 파트 추가] 중복 저장 방지용 ──────────────────────
    // clientUuid로 이미 저장된 감지 기록이 있는지 조회.
    // (앱이 같은 감지를 재전송해도 중복 저장되지 않도록 DetectionService에서 사용)
    Optional<DetectionRecord> findByClientUuid(String clientUuid);
}