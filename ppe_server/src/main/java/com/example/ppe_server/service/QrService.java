package com.example.ppe_server.service;

import com.example.ppe_server.dto.QrCodeResponse;
import com.example.ppe_server.dto.VerifyQrResponse;
import com.example.ppe_server.entity.QrToken;
import com.example.ppe_server.entity.User;
import com.example.ppe_server.repository.QrTokenRepository;
import com.example.ppe_server.repository.UserRepository;
import com.example.ppe_server.util.QrImageGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QrService {

    private final QrTokenRepository qrTokenRepository;
    private final UserRepository userRepository;
    private final QrImageGenerator qrImageGenerator;

    private static final int DEFAULT_VALID_DAYS = 1; // 기본 유효기간 1일

    // 1. QR 발급/재발급 (노동자 본인의 QR을 만든다. 1인 1개, 새로 만들면 기존 것 덮어씀)
    @Transactional
    public QrCodeResponse issueQr(String userId, Integer validDays) {
        // 사용자 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        int days = (validDays != null && validDays > 0) ? validDays : DEFAULT_VALID_DAYS;

        String token = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireAt = now.plusDays(days);

        // 이미 QR이 있으면 갱신(덮어쓰기), 없으면 새로 생성
        QrToken qrToken = qrTokenRepository.findById(user.getUserId())
                .orElse(null);

        if (qrToken == null) {
            qrToken = QrToken.builder()
                    .userId(user.getUserId())
                    .token(token)
                    .issuedAt(now)
                    .expireAt(expireAt)
                    .build();
        } else {
            qrToken.reissue(token, now, expireAt); // 기존 행 덮어쓰기 → 이전 QR 자동 무효
        }

        QrToken saved = qrTokenRepository.save(qrToken);

        // QR 이미지 생성 (토큰을 담은 QR PNG → Base64)
        String qrImageBase64 = qrImageGenerator.generateBase64(token);

        return QrCodeResponse.of(saved, qrImageBase64);
    }

    // 2. QR 검증 (게이트에서 스캔한 QR이 유효한지 + 누구인지 확인)
    @Transactional(readOnly = true)
    public VerifyQrResponse verifyQr(String token) {
        if (token == null || token.isBlank()) {
            return VerifyQrResponse.builder()
                    .valid(false)
                    .message("QR 토큰이 비어있습니다.")
                    .build();
        }

        QrToken qrToken = qrTokenRepository.findByToken(token).orElse(null);

        // 존재하지 않는 QR (또는 이미 재발급되어 무효화된 예전 토큰)
        if (qrToken == null) {
            return VerifyQrResponse.builder()
                    .valid(false)
                    .message("유효하지 않은 QR입니다.")
                    .build();
        }

        // 만료된 QR
        if (qrToken.isExpired()) {
            return VerifyQrResponse.builder()
                    .valid(false)
                    .userId(qrToken.getUserId())
                    .message("만료된 QR입니다.")
                    .build();
        }

        // 유효한 QR → 노동자 신원 정보 조회해서 반환
        User worker = userRepository.findById(qrToken.getUserId()).orElse(null);

        return VerifyQrResponse.builder()
                .valid(true)
                .userId(qrToken.getUserId())
                .name(worker != null ? worker.getName() : null)
                .siteId(worker != null ? worker.getSiteId() : null)
                .message("유효한 QR입니다.")
                .build();
    }
}
