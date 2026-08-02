package com.example.ppe_server.controller;

// 프론트에 오류처리 메시지 전달

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice //모든 컨트롤러에서 발생하는 예외를 잡아서 처리
public class GlobalExceptionHandler {

    // "IllegalArgumentException이 발생하면 이 메서드로 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)   // 400으로 변경
                .body(Map.of("message", e.getMessage())); //예외처리 메시지 전달
    }
}