package com.example.ppe_server.service;

// 회원 로직 (중복확인/비번해시/로그인/토큰발급)

import com.example.ppe_server.dto.SignupRequest;
import com.example.ppe_server.entity.User;
import com.example.ppe_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.ppe_server.dto.LoginRequest;
import com.example.ppe_server.dto.LoginResponse;
import com.example.ppe_server.util.JwtUtil;
import com.example.ppe_server.dto.FindIdRequest;
import com.example.ppe_server.dto.ResetPasswordRequest;
import com.example.ppe_server.dto.ChangePasswordRequest;
import com.example.ppe_server.entity.Purpose;
import com.example.ppe_server.dto.DeleteAccountRequest;

@Service
@RequiredArgsConstructor


public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;


    public void signup(SignupRequest request) {

        // 0. 이메일 인증 확인 (추가!)
        if (!emailService.isVerified(request.getEmail(), Purpose.SIGNUP)) {
            throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다.");
        }

        // 1. 중복 확인
        if (userRepository.existsByUserId(request.getUserId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 2. 비밀번호 해시 + User 생성
        User user = User.builder()
                .userId(request.getUserId())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))  // 해시!
                .name(request.getName())
                .nickname(request.getNickname())
                .role(request.getRole())
                .build();

        // 3. 저장
        userRepository.save(user);
    }

    // 로그인 메서드
    public LoginResponse login(LoginRequest request) {
        // 1. 아이디로 사용자 찾기
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

        // 2. 비밀번호 확인 (입력값 vs 저장된 해시)
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        // 3. 토큰 발급
        String token = jwtUtil.createToken(user.getUserId());
        return new LoginResponse(token, user.getUserId(), user.getRole().name());
    }

    // 아이디 찾기
    public String findUserId(FindIdRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 가입된 계정이 없습니다."));
        return user.getUserId();
    }

    // 비밀번호 재설정
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 가입된 계정이 없습니다."));

        // 새 비밀번호를 해시해서 변경
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // 비밀번호 변경 (로그인 상태)
    public void changePassword(String userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호로 변경
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // 회원 탈퇴
    public void deleteAccount(String userId, DeleteAccountRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 비밀번호 확인 (본인 확인)
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 현장 소속 확인 (추가!)
        if (user.getSiteId() != null) {
            throw new IllegalArgumentException("현장에 소속되어 있어 탈퇴할 수 없습니다. 현장을 나가거나 삭제한 후 탈퇴하세요.");
        }

        // 삭제
        userRepository.delete(user);
    }


}