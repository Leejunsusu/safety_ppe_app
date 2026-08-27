package com.example.ppe_server.util.ppename;

/**
 * PPE 클래스 번호(ppe_id 0~15) → 한글 이름 변환
 * YOLO 클래스 순서와 동일
 */
public class PpeNames {

    private static final String[] KO = {
            "화학용 장갑",       // 0  chemical_gloves
            "방진 마스크",       // 1  dust_mask
            "안면 보호구",       // 2  face_shield
            "각반",             // 3  gaiters
            "방독 마스크",       // 4  gas_mask
            "안전모",           // 5  helmet
            "안전모(끈 미체결)", // 6  helmet_nostrap
            "보호복",           // 7  protective_suit
            "보안경",           // 8  safety_glasses
            "안전장갑",         // 9  safety_gloves
            "안전대",           // 10 safety_harness
            "안전고리",         // 11 safety_hook
            "안전화",           // 12 safety_shoes
            "용접 각반",         // 13 welding_gaiters
            "용접 장갑",         // 14 welding_gloves
            "용접 보호면"        // 15 welding_shield
    };

    // ppe_id → 한글 이름 (범위 벗어나면 "PPE")
    public static String get(int ppeId) {
        return (ppeId >= 0 && ppeId < KO.length) ? KO[ppeId] : "PPE";
    }
}