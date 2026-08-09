// sites 테이블과 매핑되는 엔티티 (현장 정보)
package com.example.ppe_server.entity.site;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sites")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "site_id")
    private Integer siteId;

    @Column(name = "site_uuid", nullable = false, unique = true, length = 36)
    private String siteUuid;          // 내부 UUID

    @Column(name = "site_code", nullable = false, unique = true, length = 6)
    private String siteCode;          // 6자리 코드

    @Column(name = "site_name", nullable = false, length = 100)
    private String siteName;

    @Column(length = 255)
    private String address;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}