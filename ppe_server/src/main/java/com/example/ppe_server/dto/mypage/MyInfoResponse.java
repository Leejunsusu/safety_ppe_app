package com.example.ppe_server.dto.mypage;

import com.example.ppe_server.entity.Role;
import lombok.Builder;
import lombok.Getter;

/**
 * 마이페이지 - 내 정보 조회 응답
 * 이름, 역할, 소속 현장(ID + 이름)
 */
@Getter
@Builder
public class MyInfoResponse {
    private String name;        // 이름
    private Role role;          // 역할 (ADMIN / WORKER)
    private Integer siteId;     // 소속 현장 ID (없으면 null)
    private String siteName;    // 소속 현장 이름 (없으면 null)
}