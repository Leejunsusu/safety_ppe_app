package com.example.ppe_server.controller.detectionlog;

import com.example.ppe_server.dto.detectionlog.DetectionSaveRequest;
import com.example.ppe_server.service.detectionlog.DetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 감지 결과 저장 API 컨트롤러
 *
 * 앱(온디바이스 tflite)이 PPE 미착용을 확정하면
 * 이 API로 감지 결과를 전송한다. 서버는 이를 DB에 저장한다.
 *
 * [엔드포인트]
 *   POST /api/detections  → 감지 결과 1건 저장
 */
@RestController
@RequestMapping("/api/detections")
@RequiredArgsConstructor
public class DetectionController {

    private final DetectionService detectionService;

    /**
     * 감지 결과를 저장한다.
     *
     * @param request 앱이 보낸 감지 결과 (JSON)
     * @return 저장된 감지 기록의 recordId
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> saveDetection(@RequestBody DetectionSaveRequest request) {

        Long recordId = detectionService.saveDetection(request);

        // 성공 응답: 저장된 recordId를 담아서 반환
        Map<String, Object> response = Map.of(
                "success", true,
                "recordId", recordId
        );

        return ResponseEntity.ok(response);
    }
}