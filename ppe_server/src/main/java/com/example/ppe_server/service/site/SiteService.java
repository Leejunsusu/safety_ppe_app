// 현장 관련 로직 (생성, 참여, 나가기)
package com.example.ppe_server.service.site;

import com.example.ppe_server.dto.site.CreateSiteRequest;
import com.example.ppe_server.dto.site.JoinSiteRequest;
import com.example.ppe_server.entity.User;
import com.example.ppe_server.entity.site.Site;
import com.example.ppe_server.entity.Role;
import com.example.ppe_server.repository.UserRepository;
import com.example.ppe_server.repository.coloring.UserSafetyStatusRepository;
import com.example.ppe_server.repository.qr.QrTokenRepository;
import com.example.ppe_server.repository.site.SiteRepository;
import com.example.ppe_server.util.site.SiteCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.ppe_server.dto.site.WorkerResponse;
import com.example.ppe_server.dto.site.RemoveWorkerRequest;
import com.example.ppe_server.entity.coloring.UserSafetyStatus;

import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;
    private final UserRepository userRepository;
    private final QrTokenRepository qrTokenRepository;
    private final UserSafetyStatusRepository userSafetyStatusRepository;

    // 현장 생성 (관리자만)
    @Transactional
    public Site createSite(String userId, CreateSiteRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 관리자만 생성 가능
        if (user.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("현장 생성 권한이 없습니다.");
        }
        // 이미 소속 현장 있으면 생성 불가
        if (user.getSiteId() != null) {
            throw new IllegalArgumentException("이미 소속된 현장이 있습니다.");
        }

        // 중복 안 되는 6자리 코드 생성
        String code;
        do {
            code = SiteCodeGenerator.generate();
        } while (siteRepository.existsBySiteCode(code));

        // 현장 생성 (UUID 자동 생성)
        Site site = Site.builder()
                .siteUuid(UUID.randomUUID().toString())
                .siteCode(code)
                .siteName(request.getSiteName())
                .address(request.getAddress())
                .build();
        siteRepository.save(site);

        // 만든 관리자를 그 현장에 자동 소속
        user.updateSite(site.getSiteId());
        userRepository.save(user);

        return site;
    }

    // 현장 참여 (노동자 + 관리자, 코드 입력)
    @Transactional
    public Site joinSite(String userId, JoinSiteRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 이미 소속 현장 있으면 참여 불가 (관리자·노동자 공통)
        if (user.getSiteId() != null) {
            throw new IllegalArgumentException("이미 소속된 현장이 있습니다. 기존 현장에서 나간 후 참여하세요.");
        }

        // 코드로 현장 찾기
        Site site = siteRepository.findBySiteCode(request.getSiteCode())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 현장 코드입니다."));

        // 소속 설정 (자동 승인)
        user.updateSite(site.getSiteId());
        userRepository.save(user);

        return site;
    }

    // 현장 나가기
    @Transactional
    public void leaveSite(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.getSiteId() == null) {
            throw new IllegalArgumentException("소속된 현장이 없습니다.");
        }

        user.updateSite(null);   // 소속 해제
        userRepository.save(user);

        // QR 무효화 (현장 나가면 QR 삭제 → 게이트 통과 불가)
        qrTokenRepository.deleteById(user.getUserId());
    }

    // 내 현재 소속 현장 조회
    @Transactional(readOnly = true)
    public Site getMySite(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 소속 현장이 없으면 예외
        if (user.getSiteId() == null) {
            throw new IllegalArgumentException("소속된 현장이 없습니다.");
        }

        // 현장 정보 찾기
        return siteRepository.findById(user.getSiteId())
                .orElseThrow(() -> new IllegalArgumentException("현장 정보를 찾을 수 없습니다."));
    }

    // 노동자 검색 (관리자가 자기 현장의 노동자를 닉네임으로)
    @Transactional(readOnly = true)
    public WorkerResponse searchWorker(String adminUserId, String nickname) {
        // 1. 요청한 사람(관리자) 확인
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 관리자만 가능
        if (admin.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("검색 권한이 없습니다.");
        }
        // 3. 관리자가 현장에 소속돼 있어야 함
        if (admin.getSiteId() == null) {
            throw new IllegalArgumentException("소속된 현장이 없습니다.");
        }

        // 4. 같은 현장 + 그 닉네임인 노동자 찾기
        User worker = userRepository.findBySiteIdAndNickname(admin.getSiteId(), nickname)
                .orElseThrow(() -> new IllegalArgumentException("해당 현장에 그 닉네임의 노동자가 없습니다."));

        // 5. 찾은 게 노동자인지 확인 (관리자가 검색될 수도 있으니)
        if (worker.getRole() != Role.WORKER) {
            throw new IllegalArgumentException("해당 닉네임은 노동자가 아닙니다.");
        }

        return new WorkerResponse(worker);
    }

    // 노동자 소속 해제 (관리자가 자기 현장의 노동자를 내보냄)
    @Transactional
    public void removeWorker(String adminUserId, RemoveWorkerRequest request) {
        // 1. 요청한 사람(관리자) 확인
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 관리자만 가능
        if (admin.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("소속 해제 권한이 없습니다.");
        }
        // 3. 관리자가 현장에 소속돼 있어야 함
        if (admin.getSiteId() == null) {
            throw new IllegalArgumentException("소속된 현장이 없습니다.");
        }

        // 4. 대상 노동자 찾기
        User target = userRepository.findById(request.getTargetUserId())
                .orElseThrow(() -> new IllegalArgumentException("대상 노동자를 찾을 수 없습니다."));

        // 5. 대상이 관리자와 같은 현장인지 확인 (본인 현장의 노동자만)
        if (!admin.getSiteId().equals(target.getSiteId())) {
            throw new IllegalArgumentException("본인이 관리하는 현장의 노동자만 해제할 수 있습니다.");
        }
        // 6. 대상이 노동자인지 확인 (관리자끼리는 제외 불가)
        if (target.getRole() != Role.WORKER) {
            throw new IllegalArgumentException("노동자만 소속 해제할 수 있습니다.");
        }

        // 7. 소속 해제 (site_id를 NULL로)
        target.updateSite(null);
        userRepository.save(target);

        // 8. QR 무효화 (소속 해제된 노동자의 QR 삭제 → 게이트 통과 불가)
        qrTokenRepository.deleteById(target.getUserId());
    }

    // 현장 삭제 (관리자 혼자 남았을 때만)
    @Transactional
    public void deleteSite(String userId) {
        // 1. 요청자(관리자) 확인
        User admin = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 관리자만 가능
        if (admin.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("현장 삭제 권한이 없습니다.");
        }
        // 3. 소속 현장이 있어야 함
        if (admin.getSiteId() == null) {
            throw new IllegalArgumentException("소속된 현장이 없습니다.");
        }

        Integer siteId = admin.getSiteId();

        // 4. 노동자가 0명인지 확인
        long workerCount = userRepository.countBySiteIdAndRole(siteId, Role.WORKER);
        if (workerCount > 0) {
            throw new IllegalArgumentException("소속 노동자가 남아 있습니다. 모두 해제한 후 삭제할 수 있습니다.");
        }
        // 5. 관리자가 나 혼자인지 확인 (다른 관리자 0명)
        long adminCount = userRepository.countBySiteIdAndRole(siteId, Role.ADMIN);
        if (adminCount > 1) {
            throw new IllegalArgumentException("다른 관리자가 있습니다. 혼자 남았을 때만 삭제할 수 있습니다.");
        }

        // 6. 관리자 본인 소속 해제 (site_id NULL)
        admin.updateSite(null);
        userRepository.save(admin);

        // 7. 현장 삭제
        siteRepository.deleteById(siteId);
        // → 현장 관련 데이터(위반 기록 등)는 나중에 위반 기능 생기면
        //   CASCADE 또는 여기서 함께 삭제 처리
    }

    // 내 현장 노동자 수 (관리자)
    public int getWorkerCount(String adminUserId) {
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (admin.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("관리자만 조회할 수 있습니다.");
        }

        Integer siteId = admin.getSiteId();
        if (siteId == null) {
            throw new IllegalArgumentException("소속된 현장이 없습니다.");
        }

        long count = userRepository.countBySiteIdAndRole(siteId, Role.WORKER);
        return (int) count;
    }

    // 현장 노동자 목록 (Inspection 모드 - 노동자 선택용)
    public List<WorkerResponse> getSiteWorkerList(String adminUserId) {
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (admin.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("관리자만 조회할 수 있습니다.");
        }

        Integer siteId = admin.getSiteId();
        if (siteId == null) {
            throw new IllegalArgumentException("소속된 현장이 없습니다.");
        }

        return userRepository.findBySiteIdAndRole(siteId, Role.WORKER)
                .stream()
                .map(w -> {
                    boolean blocked = userSafetyStatusRepository.findById(w.getUserId())
                            .map(UserSafetyStatus::isEntryBlocked)
                            .orElse(false);
                    return new WorkerResponse(w, blocked);
                })
                .toList();
    }


}