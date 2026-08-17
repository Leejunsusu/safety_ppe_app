package com.example.ppe_server.repository.coloring;

import com.example.ppe_server.entity.coloring.UserSafetyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 사용자 안전상태(user_safety_status) 리포지토리
 *
 * [사용처] 대시보드에서 노동자 안전 등급(컬러링) 조회 (DashboardService)
 *
 * PK가 user_id(String)이므로 findById(userId)로 조회 가능.
 */
public interface UserSafetyStatusRepository extends JpaRepository<UserSafetyStatus, String> {
    // 기본 CRUD로 충분. 필요 시 커스텀 쿼리 추가.
}