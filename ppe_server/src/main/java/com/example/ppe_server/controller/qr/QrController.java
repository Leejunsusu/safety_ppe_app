package com.example.ppe_server.controller.qr;

import com.example.ppe_server.dto.qr.*;
import com.example.ppe_server.dto.qr.VerifyQrRequest;
import com.example.ppe_server.dto.qr.VerifyQrResponse;
import com.example.ppe_server.service.qr.QrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

// QR 코드 관련 요청 처리
@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
public class QrController {

    private final QrService qrService;

    // QR 발급/재발급 (로그인 필요, 본인의 QR을 만든다)
    @PostMapping("/issue")
    public ResponseEntity<QrCodeResponse> issueQr(
            @AuthenticationPrincipal String userId,
            @RequestBody(required = false) GenerateQrRequest request) {
        Integer validDays = (request != null) ? request.getValidDays() : null;
        QrCodeResponse response = qrService.issueQr(userId, validDays);
        return ResponseEntity.ok(response);
    }

    // QR 검증 (게이트에서 스캔한 토큰이 유효한지 확인)
    @PostMapping("/verify")
    public ResponseEntity<VerifyQrResponse> verifyQr(
            @RequestBody VerifyQrRequest request) {
        VerifyQrResponse response = qrService.verifyQr(request.getToken());
        return ResponseEntity.ok(response);
    }

    // 내 QR 조회 (로그인 필요, 현재 유효한 내 QR 반환)
    @GetMapping("/my")
    public ResponseEntity<QrCodeResponse> getMyQr(
            @AuthenticationPrincipal String userId) {
        QrCodeResponse response = qrService.getMyQr(userId);
        return ResponseEntity.ok(response);
    }


}