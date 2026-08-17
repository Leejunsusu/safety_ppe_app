package com.example.ppe_server.entity.qr;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// qr_tokens 테이블과 매핑되는 엔티티 (노동자별 현재 유효한 QR, 1인 1행)
// - user_id를 PK로 두어 노동자당 항상 1개만 유지
// - 새로 발급하면 기존 행을 덮어씀(UPDATE) → 이전 QR 자동 무효
// - 게이트 전용 (user_id + expire만)
@Entity
@Table(name = "qr_tokens")
@Getter
@NoArgsConstructor              // 기본 생성자
@AllArgsConstructor             // QrToken.builder() 사용
@Builder                        // QrToken.builder() 사용
public class QrToken {

    @Id
    @Column(name = "user_id", length = 64)
    private String userId;                  // 노동자 아이디 (PK, User.userId와 연결)

    @Column(nullable = false, length = 255)
    private String token;                   // 최신 QR 식별값(토큰, UUID)

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;         // 발급 시각

    @Column(name = "expire_at", nullable = false)
    private LocalDateTime expireAt;         // 만료 일시

    // 새 토큰으로 갱신 (재발급 시 기존 행 덮어쓰기)
    public void reissue(String newToken, LocalDateTime issuedAt, LocalDateTime expireAt) {
        this.token = newToken;
        this.issuedAt = issuedAt;
        this.expireAt = expireAt;
    }

    // 만료되었는지 확인
    public boolean isExpired() {
        return expireAt.isBefore(LocalDateTime.now());
    }
}