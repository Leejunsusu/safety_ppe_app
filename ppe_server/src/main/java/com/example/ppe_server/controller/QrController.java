package com.example.ppe_server.controller;

import com.example.ppe_server.dto.GenerateQrRequest;
import com.example.ppe_server.dto.QrCodeResponse;
import com.example.ppe_server.dto.VerifyQrRequest;
import com.example.ppe_server.dto.VerifyQrResponse;
import com.example.ppe_server.service.QrService;
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
}
