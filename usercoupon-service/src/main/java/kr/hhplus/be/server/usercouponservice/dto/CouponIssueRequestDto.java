package kr.hhplus.be.server.usercouponservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kafka를 통해 쿠폰 발급 성공/실패 이벤트를 전달받기 위한 DTO입니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponIssueRequestDto {
    private Long userId;
    private Long couponId;
    private String correlationId;
}
