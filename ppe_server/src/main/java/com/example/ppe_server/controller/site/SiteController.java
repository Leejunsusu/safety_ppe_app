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


}