package kr.hhplus.be.server.usercouponservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.usercouponservice.dto.CouponIssueRequestDto;
import kr.hhplus.be.server.usercouponservice.dto.UserCouponDto;
import kr.hhplus.be.server.usercouponservice.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCouponKafkaListener {

    private final UserCouponService userCouponService;
    private final UserCouponKafkaProducer userCouponKafkaProducer;
    private final ObjectMapper objectMapper;

    /**
     * coupon-service에서 쿠폰 발급 및 재고 차감이 성공했을 때,
     * coupon-issue-success 토픽으로 발행된 메시지를 수신합니다.
     * 이 메시지를 받아 사용자에게 쿠폰을 최종적으로 지급합니다.
     *
     * @param message 성공 이벤트 메시지 (JSON, CouponIssueRequestDto 형태를 기대)
     */
    @KafkaListener(topics = "coupon-issue-success", groupId = "usercoupon-group")
    public void grantCouponToUser(String message) {
        log.info("Received coupon-issue-success message: {}", message);

        String correlationId = null;
        UserCouponDto request = null;

        try {
            // 1. 성공 메시지를 DTO로 변환
            request = objectMapper.readValue(message, UserCouponDto.class);
            correlationId = request.getCorrelationId();

            // 2. 사용자에게 쿠폰을 지급하는 서비스 호출
            userCouponService.issueUserCoupon(request);

            // 3. 최종 성공 처리
            log.info("Successfully granted coupon to user. [userId={}, couponId={}, correlationId={}]",
                    request.getUserId(), request.getCouponId(), correlationId);

        } catch (Exception e) {
            // 4. 사용자 쿠폰 지급 실패 시, 보상 트랜잭션을 위해 실패 이벤트 발행
            log.error("Failed to grant coupon to user. Triggering compensation. [correlationId={}]", correlationId, e);

            if (request != null) {
                userCouponKafkaProducer.send("coupon-issue-failure", request, correlationId);
            } else {
                // 메시지 파싱 자체를 실패한 경우
                log.error("Cannot trigger compensation because the message could not be parsed: {}", message);
            }
        }
    }
}
