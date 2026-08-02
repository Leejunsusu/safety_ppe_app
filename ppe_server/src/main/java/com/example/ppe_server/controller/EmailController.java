// 이메일 인증 관련 요청 처리 (코드 발송/검증)
package com.example.ppe_server.controller;

import com.example.ppe_server.dto.EmailSendRequest;
import com.example.ppe_server.dto.EmailVerifyRequest;
import com.example.ppe_server.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    // 인증코드 발송
    @PostMapping("/send")
    public ResponseEntity<String> send(@RequestBody EmailSendRequest request) {
        emailService.sendCode(request.getEmail(), request.getPurpose());
        return ResponseEntity.ok("인증코드가 발송되었습니다.");
    }

    // 인증코드 검증
    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestBody EmailVerifyRequest request) {
        emailService.verifyCode(request.getEmail(), request.getCode(), request.getPurpose());
        return ResponseEntity.ok("인증이 완료되었습니다.");
    }
}