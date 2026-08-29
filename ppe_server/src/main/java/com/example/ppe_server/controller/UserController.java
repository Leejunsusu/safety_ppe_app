package com.example.ppe_server.controller;

// 회원 관련 요청 처리 (회원가입/로그인 등)

import com.example.ppe_server.dto.SignupRequest;
import com.example.ppe_server.dto.alarm.FcmTokenRequest;
import com.example.ppe_server.dto.mypage.MyInfoResponse;
import com.example.ppe_server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.ppe_server.dto.LoginRequest;
import com.example.ppe_server.dto.LoginResponse;
import com.example.ppe_server.dto.FindIdRequest;
import com.example.ppe_server.dto.ResetPasswordRequest;
import com.example.ppe_server.dto.ChangePasswordRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.example.ppe_server.dto.DeleteAccountRequest;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
        userService.signup(request);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    // 아이디 찾기
    @PostMapping("/find-id")
    public ResponseEntity<String> findId(@RequestBody FindIdRequest request) {
        String userId = userService.findUserId(request);
        return ResponseEntity.ok("회원님의 아이디는 [" + userId + "] 입니다.");
    }

    // 비밀번호 재설정
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.ok("비밀번호가 재설정되었습니다.");
    }

    // 비밀번호 변경 (로그인 필요)
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal String userId,
            @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userId, request);
        return ResponseEntity.ok("비밀번호가 변경되었습니다.");
    }

    // 회원 탈퇴 (로그인 필요)
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteAccount(
            @AuthenticationPrincipal String userId,
            @RequestBody DeleteAccountRequest request) {
        userService.deleteAccount(userId, request);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }

    // 마이페이지 - 내 정보 조회 (로그인 필요)
    @GetMapping("/me")
    public ResponseEntity<MyInfoResponse> getMyInfo(
            @AuthenticationPrincipal String userId) {
        MyInfoResponse response = userService.getMyInfo(userId);
        return ResponseEntity.ok(response);
    }

    // 아이디 중복 확인
    @GetMapping("/check-id")
    public ResponseEntity<Boolean> checkUserId(@RequestParam String userId) {
        boolean available = userService.isUserIdAvailable(userId);
        return ResponseEntity.ok(available);   // true=사용가능, false=중복
    }

    // 닉네임 중복 확인
    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        boolean available = userService.isNicknameAvailable(nickname);
        return ResponseEntity.ok(available);   // true=사용가능, false=중복
    }

    // FCM 토큰 저장 (로그인 필요)
    @PostMapping("/fcm-token")
    public ResponseEntity<String> updateFcmToken(
            @AuthenticationPrincipal String userId,
            @RequestBody FcmTokenRequest request) {
        userService.updateFcmToken(userId, request.getFcmToken());
        return ResponseEntity.ok("FCM 토큰이 저장되었습니다.");
    }


}