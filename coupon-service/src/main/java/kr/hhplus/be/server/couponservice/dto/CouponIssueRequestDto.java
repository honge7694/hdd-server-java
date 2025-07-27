package kr.hhplus.be.server.couponservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kafka를 통해 쿠폰 발급 요청 및 실패 이벤트를 전달하기 위한 DTO입니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponIssueRequestDto {
    private Long userId;
    private Long couponId;
    private String correlationId;
}
