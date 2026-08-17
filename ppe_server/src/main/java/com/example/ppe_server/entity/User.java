package com.example.ppe_server.entity;

// users 테이블과 매핑되는 엔티티 (회원 정보)

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")   // users 테이블과 연결
@Getter  // getter 메서드
@NoArgsConstructor // 기본생성자 자동으로 만들어준다
@AllArgsConstructor // User.builder() 사용
@Builder // User.builder() 사용
public class User {

    @Id   // 기본키
    @Column(name = "user_id", length = 64) //  @Column 각 필드가 어느 칼럼인지 연결
    private String userId;              // 로그인 아이디 (PK)

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;        // 해시된 비밀번호

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, unique = true, length = 64)
    private String nickname;

    @Enumerated(EnumType.STRING) // role을 문자열 ADMIN/WORKER로 저장해라
    @Column(nullable = false)
    private Role role;                  // ADMIN / WORKER

    @Column(name = "site_id")
    private Integer siteId;             // 소속 현장 (없으면 null)

    @Column(name = "fcm_token")
    private String fcmToken;

    @Column(name = "notification_enabled", nullable = false)
    private boolean notificationEnabled = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist  // 저장되기 직전 create_at를 현재시각으로 자동설정
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // 비밀번호 변경
    public void updatePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

    // 소속 현장 변경 (참여/나가기)
    public void updateSite(Integer siteId) {
        this.siteId = siteId;
    }

    // 알림 수신 여부 변경 (알림 설정 ON/OFF)
    public void updateNotificationEnabled(boolean enabled) {
        this.notificationEnabled = enabled;
    }

}