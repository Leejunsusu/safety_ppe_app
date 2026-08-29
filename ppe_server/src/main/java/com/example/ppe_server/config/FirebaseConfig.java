package com.example.ppe_server.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Firebase 초기화 설정
 * 서버 시작 시 서비스 계정 키로 Firebase에 연결
 */
@Configuration
public class FirebaseConfig {

    // secret에서 키 파일 경로 읽기
    @Value("${firebase.key-path}")
    private String firebaseKeyPath;

    @PostConstruct  // 서버 시작 시 자동 실행
    public void initialize() {
        try {
            // 키 파일 읽기
            InputStream serviceAccount = new FileInputStream(firebaseKeyPath);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // 이미 초기화됐으면 중복 방지
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase 초기화 성공");
            }
        } catch (Exception e) {
            System.err.println("Firebase 초기화 실패: " + e.getMessage());
        }
    }
}