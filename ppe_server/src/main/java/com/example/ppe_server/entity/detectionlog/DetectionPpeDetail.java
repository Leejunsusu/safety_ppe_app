package com.example.ppe_server.entity.detectionlog;

import jakarta.persistence.*;
import lombok.*;

/**
 * 미착용(PPE) 상세 항목 엔티티
 *
 * [DB 매핑] detection_ppe_details 테이블
 *
 * 감지 기록(DetectionRecord) 1건에 대해, 어떤 PPE가 착용/미착용이었는지를
 * 항목별로 저장한다. 감지 기록 1건 : 상세 항목 여러 건 (1:N 관계)
 *
 * - record_id : 어느 감지 기록에 속하는지 (detection_records.record_id 참조)
 * - ppe_id    : 어떤 PPE 항목인지 (ppe_items.ppe_id 참조)
 * - worn      : 착용 여부 (true=착용, false=미착용)
 * - confidence: 감지 신뢰도 (0.000 ~ 0.999 등, 소수점 3자리)
 *
 * ※ 친구가 만든 DetectionRecord와 동일한 방식(연관관계 대신 id를 직접 필드로)으로 작성.
 */
@Entity
@Table(name = "detection_ppe_details")
@Getter
@NoArgsConstructor              // 기본 생성자
@AllArgsConstructor             // 빌더용 전체 생성자
@Builder                        // DetectionPpeDetail.builder() 사용
public class DetectionPpeDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    @Column(name = "detail_id")
    private Long detailId;                   // 상세 항목 PK

    @Column(name = "record_id", nullable = false)
    private Long recordId;                    // 소속 감지 기록 (DetectionRecord.recordId)

    @Column(name = "ppe_id", nullable = false)
    private Integer ppeId;                    // PPE 항목 (ppe_items.ppe_id)

    @Column(name = "worn", nullable = false)
    private Boolean worn;                     // 착용 여부 (true=착용, false=미착용)

    @Column(name = "confidence")
    private java.math.BigDecimal confidence;  // 감지 신뢰도 (decimal(5,3))
}