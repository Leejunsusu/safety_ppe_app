package com.example.ppe_server.repository;

import com.example.ppe_server.entity.QrToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QrTokenRepository extends JpaRepository<QrToken, String> {

    // 토큰으로 QR 찾기 (게이트에서 QR 검증 시 사용)
    Optional<QrToken> findByToken(String token);
}
