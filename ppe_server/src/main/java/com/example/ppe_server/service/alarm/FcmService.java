package com.example.ppe_server.service.alarm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

/**
 * FCM 푸시 발송 서비스
 */
@Service
public class FcmService {

    /**
     * 특정 기기(토큰)에 푸시 알림 발송
     * @param fcmToken 받는 사람의 FCM 토큰
     * @param title 알림 제목
     * @param body 알림 내용
     */
    public void sendNotification(String fcmToken, String title, String body) {
        // 토큰 없으면 발송 안 함 (앱이 토큰 저장 안 한 사용자)
        if (fcmToken == null || fcmToken.isBlank()) {
            return;
        }

        try {
            // 알림 내용 구성
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            // 메시지 구성 (받는 사람 토큰 + 알림)
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(notification)
                    .build();

            // 발송
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("FCM 발송 성공: " + response);

        } catch (Exception e) {
            // 발송 실패해도 서버는 계속 (알림 하나 실패가 전체를 막지 않게)
            System.err.println("FCM 발송 실패: " + e.getMessage());
        }
    }
}