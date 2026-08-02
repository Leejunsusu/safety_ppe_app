// 이메일 인증 용도 구분 enum (회원가입/아이디찾기/비밀번호재설정)
package com.example.ppe_server.entity;

public enum Purpose {
    SIGNUP,      // 회원가입
    FIND_ID,     // 아이디 찾기
    RESET_PW     // 비밀번호 재설정
}