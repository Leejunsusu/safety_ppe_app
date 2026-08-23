package com.example.ppe_server.controller.s3;

import com.example.ppe_server.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * S3 업로드 URL 발급 API
 *
 * 앱이 위반 사진을 S3에 올리기 전에, 이 API로 업로드용 URL을 받는다.
 * 흐름: 앱 → 이 API 호출 → 업로드 URL + s3Key 받음
 *      → 그 URL로 S3에 사진 직접 업로드
 *      → 감지 저장 API 호출 시 s3Key 포함
 */
@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    /**
     * 업로드용 Presigned URL 발급
     * @return { "uploadUrl": "...", "s3Key": "..." }
     */
    @PostMapping("/upload-url")
    public ResponseEntity<Map<String, String>> getUploadUrl() {
        String[] result = s3Service.createUploadUrl();
        Map<String, String> response = Map.of(
                "uploadUrl", result[0],   // 앱이 이 URL로 사진 업로드
                "s3Key", result[1]        // 감지 저장 때 서버에 보낼 값
        );
        return ResponseEntity.ok(response);
    }
}