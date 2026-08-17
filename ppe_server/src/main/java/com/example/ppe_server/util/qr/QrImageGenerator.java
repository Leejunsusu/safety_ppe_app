package com.example.ppe_server.util.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

// ZXing 라이브러리로 QR 이미지를 생성하는 유틸
@Component
public class QrImageGenerator {

    private static final int SIZE = 300; // QR 이미지 한 변 크기(px)

    // 주어진 내용(content)으로 QR PNG 이미지를 만들어 Base64 문자열로 반환
    public String generateBase64(String content) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1); // 여백 최소화

            BitMatrix matrix = new MultiFormatWriter()
                    .encode(content, BarcodeFormat.QR_CODE, SIZE, SIZE, hints);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);

            // 앱에서 <img src="data:image/png;base64,..."> 형태로 바로 쓸 수 있게 반환
            return Base64.getEncoder().encodeToString(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("QR 이미지 생성 실패", e);
        }
    }
}