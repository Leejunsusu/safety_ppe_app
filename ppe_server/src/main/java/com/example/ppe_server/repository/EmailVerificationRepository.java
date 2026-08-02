// email_verifications 테이블 접근 (JPA)
package com.example.ppe_server.repository;

import com.example.ppe_server.entity.EmailVerification;
import com.example.ppe_server.entity.Purpose;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    // 이메일 + 용도로 가장 최근 인증 정보 찾기
    Optional<EmailVerification> findTopByEmailAndPurposeOrderByCreatedAtDesc(String email, Purpose purpose);
}