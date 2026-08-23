package com.example.ppe_server.repository.detectionlog;

import com.example.ppe_server.entity.detectionlog.DetectionPpeDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 미착용 상세 항목(detection_ppe_details) 저장/조회용 리포지토리
 *
 * JpaRepository를 상속하면 save(), findAll(), findById() 등
 * 기본 CRUD 메서드가 자동으로 생긴다. (직접 구현할 필요 없음)
 */
public interface DetectionPpeDetailRepository extends JpaRepository<DetectionPpeDetail, Long> {

    // 특정 감지 기록(recordId)에 속한 상세 항목들을 모두 조회
    List<DetectionPpeDetail> findByRecordId(Long recordId);
}