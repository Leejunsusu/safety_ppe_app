// 이메일 인증코드 발송/검증 로직
package com.example.ppe_server.service;

import com.example.ppe_server.entity.EmailVerification;
import com.example.ppe_server.entity.Purpose;
import com.example.ppe_server.repository.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailVerificationRepository verificationRepository;

    // 인증코드 발송
    public void sendCode(String email, Purpose purpose) {
        // 1. 6자리 코드 생성
        String code = String.format("%06d", new Random().nextInt(1000000));

        // 2. DB 저장 (만료 3분)
        EmailVerification verification = EmailVerification.builder()
                .email(email)
                .code(code)
                .purpose(purpose)
                .expireAt(LocalDateTime.now().plusMinutes(3))
                .verified(false)
                .build();
        verificationRepository.save(verification);

        // 3. 이메일 발송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[AI 스마트 안전 메이트] 인증코드");
        message.setText("인증코드는 [" + code + "] 입니다. 3분 이내에 입력해주세요.");
        mailSender.send(message);
    }

    // 인증코드 검증
    public boolean verifyCode(String email, String code, Purpose purpose) {
        EmailVerification verification = verificationRepository
                .findTopByEmailAndPurposeOrderByCreatedAtDesc(email, purpose)
                .orElseThrow(() -> new IllegalArgumentException("인증 요청이 없습니다."));

        // 만료 확인
        if (verification.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("인증코드가 만료되었습니다.");
        }
        // 코드 일치 확인
        if (!verification.getCode().equals(code)) {
            throw new IllegalArgumentException("인증코드가 일치하지 않습니다.");
        }

        // 인증 완료 표시
        verification.setVerified(true);
        verificationRepository.save(verification);
        return true;
    }

    // 이메일이 해당 용도로 인증 완료됐는지 확인
    public boolean isVerified(String email, Purpose purpose) {
        return verificationRepository
                .findTopByEmailAndPurposeOrderByCreatedAtDesc(email, purpose)
                .map(EmailVerification::isVerified)   // 가장 최근 기록의 verified 값
                .orElse(false);                        // 기록 없으면 false
    }


}