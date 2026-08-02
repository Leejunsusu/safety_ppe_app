// email_verifications 테이블과 매핑되는 엔티티 (이메일 인증코드)
package com.example.ppe_server.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_verifications")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verification_id")
    private Long verificationId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 6)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Purpose purpose;          // SIGNUP / FIND_ID / RESET_PW

    @Column(name = "expire_at", nullable = false)
    private LocalDateTime expireAt;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}