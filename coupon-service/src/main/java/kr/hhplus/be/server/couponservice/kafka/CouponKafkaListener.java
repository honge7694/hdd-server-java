package kr.hhplus.be.server.couponservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.couponservice.dto.CouponIssueRequestDto; // 실패 메시지에 포함될 DTO
import kr.hhplus.be.server.couponservice.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 쿠폰 발급 실패에 대한 보상 트랜잭션을 처리하는 Kafka 리스너입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CouponKafkaListener {

    private final CouponService couponService;
    private final ObjectMapper objectMapper;

    /**
     * usercoupon-service에서 쿠폰 지급이 실패했을 때, coupon-issue-failure 토픽으로 발행된 메시지를 수신합니다.
     * 이 메시지를 받아 쿠폰 재고를 다시 원복하는 보상 로직을 실행합니다.
     *
     * @param message 실패 이벤트 메시지 (JSON, CouponIssueRequestDto 형태를 기대)
     */
    @KafkaListener(topics = "coupon-issue-failure", groupId = "coupon-group-compensate")
    public void compensateCouponIssuance(String message) {
        log.warn("Received coupon-issue-failure message for compensation: {}", message);

        String correlationId = null;
        try {
            // 1. 실패 메시지를 DTO로 변환
            CouponIssueRequestDto request = objectMapper.readValue(message, CouponIssueRequestDto.class);
            correlationId = request.getCorrelationId();

            // 2. 쿠폰 재고를 원복하는 보상 서비스 호출
            couponService.compensateCouponIssuance(request.getCouponId());

            log.info("Successfully compensated coupon stock. [couponId={}, correlationId={}]", request.getCouponId(), correlationId);

        } catch (Exception e) {
            // 3. 보상 트랜잭션 자체도 실패한 경우
            log.error("Critical: Failed to compensate coupon stock. Manual intervention may be required. [correlationId={}]", correlationId, e);
        }
    }
}

