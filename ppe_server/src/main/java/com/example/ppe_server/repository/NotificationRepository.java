package com.example.ppe_server.repository;

import com.example.ppe_server.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 알림(notifications) 리포지토리
 *
 * [사용처] 알림 이력 조회 / 읽음 처리 (NotificationService)
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 특정 사용자가 받은 알림을 최신순으로 조회 (알림 이력 화면)
    List<Notification> findByReceiverUserIdOrderBySentAtDesc(String receiverUserId);

    // 특정 사용자의 '안 읽은' 알림 개수 (알림 뱃지 표시용)
    long countByReceiverUserIdAndIsReadFalse(String receiverUserId);
}
