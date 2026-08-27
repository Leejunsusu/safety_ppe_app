package com.example.ppe_server.service.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;


import java.time.Duration;
import java.util.UUID;

/**
 * S3 Presigned URL 발급 서비스
 *
 * 앱이 위반 사진을 S3에 직접 업로드할 수 있도록,
 * 임시 업로드 URL(Presigned URL)을 발급한다.
 */
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucket;

    /**
     * 업로드용 Presigned URL 발급
     *
     * @return [0]=업로드할 URL, [1]=저장될 s3Key (감지 저장 때 서버에 보낼 값)
     */
    public String[] createUploadUrl() {
        // 1. 사진이 저장될 경로(s3Key) 생성 - 중복 안 되게 UUID 사용
        String s3Key = "violations/" + UUID.randomUUID() + ".jpg";

        // 2. PUT 요청용 Presigned URL 만들기 (5분간 유효)
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(s3Key)
                .contentType("image/jpeg")
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))   // 5분 유효
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);

        // 3. [업로드 URL, s3Key] 반환
        return new String[]{ presigned.url().toString(), s3Key };
    }

    /**
     * 조회용 Presigned URL 발급 (대시보드에서 사진 볼 때)
     * @param s3Key 저장된 사진 경로 (DB의 s3Key)
     * @return 사진을 볼 수 있는 임시 URL (5분 유효)
     */
    public String createViewUrl(String s3Key) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(s3Key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))   // 5분 유효
                .getObjectRequest(objectRequest)
                .build();

        PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(presignRequest);

        return presigned.url().toString();
    }


}