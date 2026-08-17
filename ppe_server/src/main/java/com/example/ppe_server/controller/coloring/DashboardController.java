package com.example.ppe_server.controller.coloring;

import com.example.ppe_server.dto.coloring.ManagerDashboardResponse;
import com.example.ppe_server.dto.coloring.WorkerDashboardResponse;
import com.example.ppe_server.service.coloring.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 대시보드 관련 API
 *
 * [엔드포인트]
 *   GET /api/dashboard/manager   관리자 대시보드 (FR-021, ADMIN만)
 *   GET /api/dashboard/worker    노동자 대시보드 (FR-022, 본인 것만)
 *
 * ※ 로그인 필요. 로그인한 userId를 기준으로 조회하므로
 *   - 관리자 대시보드: 서비스에서 ADMIN 권한 체크
 *   - 노동자 대시보드: 본인 데이터만 반환 (타인 접근 불가)
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // 관리자 대시보드 (소속 현장 노동자 안전등급 목록 + 오늘 통계)
    @GetMapping("/manager")
    public ResponseEntity<ManagerDashboardResponse> getManagerDashboard(
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(dashboardService.getManagerDashboard(userId));
    }

    // 노동자 대시보드 (본인 안전등급 + 미착용 이력 + 알림 이력)
    @GetMapping("/worker")
    public ResponseEntity<WorkerDashboardResponse> getWorkerDashboard(
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(dashboardService.getWorkerDashboard(userId));
    }
}