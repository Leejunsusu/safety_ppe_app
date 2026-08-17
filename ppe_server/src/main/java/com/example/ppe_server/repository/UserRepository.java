package com.example.ppe_server.repository;

// users 테이블 조회/저장 (JPA)

import com.example.ppe_server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.example.ppe_server.entity.Role;

// JpaRepository를 상속하면, 기본적인 DB 작업(저장, 조회, 삭제 등)이 자동으로 생성됨
public interface UserRepository extends JpaRepository<User, String> {

    // 아이디(user_id) 존재 여부 확인 → 회원가입 중복 확인용
    boolean existsByUserId(String userId);

    // 이메일 존재 여부 확인 → 이메일 중복 확인용
    boolean existsByEmail(String email);

    // 닉네임 존재 여부 확인 → 닉네임 중복 확인용
    boolean existsByNickname(String nickname);

    // 이메일로 사용자 찾기 → 아이디 찾기 / 비밀번호 재설정용
    Optional<User> findByEmail(String email);
    // 특정 현장에 속한 특정 역할의 사용자 목록 (관리자 대시보드용)
    java.util.List<User> findBySiteIdAndRole(Integer siteId, Role role);

}