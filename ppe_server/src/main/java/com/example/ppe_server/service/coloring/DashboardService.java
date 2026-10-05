package com.example.ppe_server.service.coloring;

import com.example.ppe_server.dto.alarm.NotificationResponse;
import com.example.ppe_server.dto.coloring.ManagerDashboardResponse;
import com.example.ppe_server.dto.coloring.WorkerDashboardResponse;
import com.example.ppe_server.dto.coloring.WorkerSafetyResponse;
import com.example.ppe_server.dto.detectionlog.DetectionRecordResponse;
import com.example.ppe_server.entity.*;
import com.example.ppe_server.entity.coloring.SafetyGrade;
import com.example.ppe_server.entity.coloring.UserSafetyStatus;
import com.example.ppe_server.repository.*;
import com.example.ppe_server.repository.alarm.NotificationRepository;
import com.example.ppe_server.repository.coloring.UserSafetyStatusRepository;
import com.example.ppe_server.repository.detectionlog.DetectionPpeDetailRepository;
import com.example.ppe_server.repository.detectionlog.DetectionRecordRepository;
import com.example.ppe_server.repository.site.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.ppe_server.util.ppename.PpeNames;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * 대시보드 서비스
 *
 * [담당 기능]
 *   - FR-021 관리자 대시보드: 소속 현장 노동자 안전 등급 목록 + 오늘 통계
 *   - FR-022 노동자 대시보드: 본인 안전 등급 + 미착용 이력 + 알림 이력
 *
 * [권한/보안]
 *   - 관리자 대시보드: ADMIN 역할만 접근 가능
 *   - 노동자 대시보드: 본인 데이터만 조회 (컨트롤러에서 로그인 userId를 그대로 사용하므로
 *     다른 노동자 데이터에 접근할 수 없음 → FR-022 "본인 데이터만" 충족)
 *
 * [뼈대 안내 - 감지 파트 연동 필요]
 *   - '오늘 미착용 건수'는 detection_records로 집계 가능 (구현함).
 *   - '오늘 총 입장/정상 통과 건수'는 detection_records가 위반만 저장하므로 알 수 없음.
 *     감지 파트가 정상 통과도 집계해줘야 정확히 채울 수 있어 지금은 0으로 둠. (TODO)
 *   - 노동자별 미착용 '항목'(안전모 등)은 detection_ppe_details 연동 필요. (추후)
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final UserSafetyStatusRepository safetyStatusRepository;
    private final DetectionRecordRepository detectionRecordRepository;
    private final NotificationRepository notificationRepository;
    private final SiteRepository siteRepository;
    private final DetectionPpeDetailRepository detectionPpeDetailRepository;
    // [현장 파트 연동 안내]
    //   관리자 대시보드에서 '현장 이름(siteName)'을 보여주려면 현장(Site) 정보가 필요하다.
    //   현장 파트는 다른 팀원이 담당하며, SiteRepository는 별도 패키지(repository.site)에 있다.
    //   지금은 결합을 피하기 위해 SiteRepository를 주입하지 않고 siteName은 비워 둔다(null).
    //   추후 현장 파트와 통합할 때, 아래 getManagerDashboard()에서
    //   siteRepository.findById(siteId)로 현장 이름을 채우면 된다.

    // ===================== FR-021 관리자 대시보드 =====================

    @Transactional(readOnly = true)
    public ManagerDashboardResponse getManagerDashboard(String userId) {
        User admin = getUser(userId);

        // 권한 확인: 안전관리자(ADMIN)만 접근 가능
        if (admin.getRole() != Role.ADMIN) {
            throw new IllegalStateException("관리자 대시보드는 안전관리자만 접근할 수 있습니다.");
        }

        Integer siteId = admin.getSiteId();
        if (siteId == null) {
            throw new IllegalStateException("소속된 현장이 없습니다. 먼저 현장을 생성하거나 합류하세요.");
        }

        // 현장 이름 조회 (현장 파트 SiteRepository 연결)
        String siteName = siteRepository.findById(siteId)
                .map(site -> site.getSiteName())
                .orElse(null);

        // 이 현장에 소속된 노동자(WORKER) 목록 조회
        // ★ UserRepository에 아래 메서드가 있어야 함 (없으면 추가 필요):
        //     List<User> findBySiteIdAndRole(Integer siteId, Role role);
        List<User> workers = userRepository.findBySiteIdAndRole(siteId, Role.WORKER);

        // 각 노동자의 안전 등급을 붙여서 응답 목록 생성
        List<WorkerSafetyResponse> workerList = workers.stream()
                .map(w -> {
                    // 안전상태 행이 아직 없으면 기본값(GREEN, 0건)으로 취급
                    UserSafetyStatus status = safetyStatusRepository.findById(w.getUserId())
                            .orElse(UserSafetyStatus.createDefault(w.getUserId()));
                    return WorkerSafetyResponse.builder()
                            .userId(w.getUserId())
                            .name(w.getName())
                            .nickname(w.getNickname())
                            .violationCount(status.getViolationCount())
                            .grade(status.getGrade())
                            .build();
                })
                // 명세서: RED 등급이 목록 최상단. RED > YELLOW > GREEN 순, 같으면 위반 많은 순
                .sorted(Comparator
                        .comparing(WorkerSafetyResponse::getGrade, Comparator.comparingInt(DashboardService::gradePriority))
                        .thenComparing(Comparator.comparingInt(WorkerSafetyResponse::getViolationCount).reversed()))
                .toList();

        // ★★★ 여기 추가 — 등급별 인원 수 집계 ★★★
        long greenCount = workerList.stream()
                .filter(w -> w.getGrade() == SafetyGrade.GREEN)
                .count();
        long yellowCount = workerList.stream()
                .filter(w -> w.getGrade() == SafetyGrade.YELLOW)
                .count();
        long redCount = workerList.stream()
                .filter(w -> w.getGrade() == SafetyGrade.RED)
                .count();

        // 오늘 하루 범위 계산 (00:00:00 ~ 다음날 00:00:00)
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);

        // 오늘 미착용 건수 (detection_records로 집계 가능)
        long todayViolation = detectionRecordRepository
                .countBySiteIdAndDetectedAtBetween(siteId, todayStart, todayEnd);

        return ManagerDashboardResponse.builder()
                .siteId(siteId)
                .siteName(siteName)
                .greenCount(greenCount)
                .yellowCount(yellowCount)
                .redCount(redCount)
                .todayViolationCount(todayViolation)
                .workers(workerList)
                .build();
    }

    // ===================== FR-022 노동자 대시보드 =====================

    @Transactional(readOnly = true)
    public WorkerDashboardResponse getWorkerDashboard(String userId) {
        // 로그인한 본인 정보만 사용 → 다른 노동자 데이터 접근 불가 (FR-022 보안 요건 충족)
        User worker = getUser(userId);

        // 본인 안전 등급 (없으면 기본값 GREEN)
        UserSafetyStatus status = safetyStatusRepository.findById(userId)
                .orElse(UserSafetyStatus.createDefault(userId));

        // 본인 미착용 이력 (최신순)
        List<DetectionRecordResponse> history = detectionRecordRepository
                .findByTargetUserIdOrderByDetectedAtDesc(userId)
                .stream()
                .map(record -> {
                    // 이 기록의 미착용 장비 이름 목록 만들기
                    List<String> notWornItems = detectionPpeDetailRepository
                            .findByRecordId(record.getRecordId())
                            .stream()
                            .filter(d -> Boolean.FALSE.equals(d.getWorn()))  // 미착용만
                            .map(d -> PpeNames.get(d.getPpeId()))            // 번호→이름
                            .toList();
                    return DetectionRecordResponse.from(record, notWornItems);
                })
                .toList();

        // 본인이 받은 알림 이력 (최신순)
        List<NotificationResponse> notis = notificationRepository
                .findByReceiverUserIdOrderBySentAtDesc(userId)
                .stream()
                .map(NotificationResponse::from)
                .toList();

        return WorkerDashboardResponse.builder()
                .userId(worker.getUserId())
                .name(worker.getName())
                .violationCount(status.getViolationCount())
                .grade(status.getGrade())
                .history(history)
                .notifications(notis)
                .build();
    }

    // ===================== 관리자가 특정 노동자 상세 조회 =====================
    @Transactional(readOnly = true)
    public WorkerDashboardResponse getWorkerDashboardByAdmin(String adminUserId, String targetUserId) {
        // 1. 관리자 본인 확인
        User admin = getUser(adminUserId);
        if (admin.getRole() != Role.ADMIN) {
            throw new IllegalStateException("관리자만 조회할 수 있습니다.");
        }

        // 2. 대상 노동자 조회
        User worker = getUser(targetUserId);

        // 3. 같은 현장인지 확인 (다른 현장 노동자 못 보게)
        if (admin.getSiteId() == null || !admin.getSiteId().equals(worker.getSiteId())) {
            throw new IllegalStateException("같은 현장의 노동자만 조회할 수 있습니다.");
        }

        // 4. 대상 노동자의 안전 등급
        UserSafetyStatus status = safetyStatusRepository.findById(targetUserId)
                .orElse(UserSafetyStatus.createDefault(targetUserId));

        // 5. 대상 노동자의 미착용 이력 (장비 이름 포함)
        List<DetectionRecordResponse> history = detectionRecordRepository
                .findByTargetUserIdOrderByDetectedAtDesc(targetUserId)
                .stream()
                .map(record -> {
                    List<String> notWornItems = detectionPpeDetailRepository
                            .findByRecordId(record.getRecordId())
                            .stream()
                            .filter(d -> Boolean.FALSE.equals(d.getWorn()))
                            .map(d -> PpeNames.get(d.getPpeId()))
                            .toList();
                    return DetectionRecordResponse.from(record, notWornItems);
                })
                .toList();

        // 6. 대상 노동자가 받은 알림 이력
        List<NotificationResponse> notis = notificationRepository
                .findByReceiverUserIdOrderBySentAtDesc(targetUserId)
                .stream()
                .map(NotificationResponse::from)
                .toList();

        return WorkerDashboardResponse.builder()
                .userId(worker.getUserId())
                .name(worker.getName())
                .violationCount(status.getViolationCount())
                .grade(status.getGrade())
                .history(history)
                .notifications(notis)
                .build();
    }

    // 관리자가 노동자 출입 차단 해제
    @Transactional
    public void unblockWorker(String adminUserId, String targetUserId) {
        // 관리자 본인 확인
        User admin = getUser(adminUserId);
        if (admin.getRole() != Role.ADMIN) {
            throw new IllegalStateException("관리자만 해제할 수 있습니다.");
        }

        // 대상 노동자 조회
        User worker = getUser(targetUserId);

        // 같은 현장인지 확인
        if (admin.getSiteId() == null || !admin.getSiteId().equals(worker.getSiteId())) {
            throw new IllegalStateException("같은 현장의 노동자만 해제할 수 있습니다.");
        }

        // 안전상태 조회 → 출입 차단 해제
        UserSafetyStatus status = safetyStatusRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("해당 노동자의 안전 정보가 없습니다."));
        status.unblock();
        safetyStatusRepository.save(status);
    }

    // ===================== 내부 헬퍼 =====================

    private User getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    // 정렬 우선순위: RED(0) < YELLOW(1) < GREEN(2) → 숫자가 작을수록 위로
    private static int gradePriority(SafetyGrade grade) {
        return switch (grade) {
            case RED -> 0;
            case YELLOW -> 1;
            case GREEN -> 2;
        };
    }
}