// 현장 관련 요청 처리 (생성, 참여, 나가기)
package com.example.ppe_server.controller.site;

import com.example.ppe_server.dto.site.*;
import com.example.ppe_server.entity.site.Site;
import com.example.ppe_server.service.site.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.ppe_server.dto.site.WorkerResponse;
import com.example.ppe_server.dto.site.RemoveWorkerRequest;


@RestController
@RequestMapping("/api/sites")
@RequiredArgsConstructor
public class SiteController {

    private final SiteService siteService;

    // 현장 생성 (관리자)
    @PostMapping
    public ResponseEntity<SiteResponse> createSite(
            @AuthenticationPrincipal String userId,
            @RequestBody CreateSiteRequest request) {
        Site site = siteService.createSite(userId, request);
        return ResponseEntity.ok(new SiteResponse(site));
    }

    // 현장 참여 (코드 입력)
    @PostMapping("/join")
    public ResponseEntity<SiteResponse> joinSite(
            @AuthenticationPrincipal String userId,
            @RequestBody JoinSiteRequest request) {
        Site site = siteService.joinSite(userId, request);
        return ResponseEntity.ok(new SiteResponse(site));
    }

    // 현장 나가기
    @DeleteMapping("/leave")
    public ResponseEntity<String> leaveSite(
            @AuthenticationPrincipal String userId) {
        siteService.leaveSite(userId);
        return ResponseEntity.ok("현장에서 나갔습니다.");
    }

    // 내 현장 조회
    @GetMapping("/my")
    public ResponseEntity<SiteResponse> getMySite(
            @AuthenticationPrincipal String userId) {
        Site site = siteService.getMySite(userId);
        return ResponseEntity.ok(new SiteResponse(site));
    }

    // 노동자 검색 (관리자)
    @GetMapping("/workers/search")
    public ResponseEntity<WorkerResponse> searchWorker(
            @AuthenticationPrincipal String adminUserId,
            @RequestParam String nickname) {
        WorkerResponse worker = siteService.searchWorker(adminUserId, nickname);
        return ResponseEntity.ok(worker);
    }

    // 노동자 소속 해제 (관리자)
    @DeleteMapping("/workers")
    public ResponseEntity<String> removeWorker(
            @AuthenticationPrincipal String adminUserId,
            @RequestBody RemoveWorkerRequest request) {
        siteService.removeWorker(adminUserId, request);
        return ResponseEntity.ok("노동자 소속이 해제되었습니다.");
    }

    // 현장 삭제 (관리자, 혼자 남았을 때)
    @DeleteMapping
    public ResponseEntity<String> deleteSite(
            @AuthenticationPrincipal String userId) {
        siteService.deleteSite(userId);
        return ResponseEntity.ok("현장이 삭제되었습니다.");
    }


}