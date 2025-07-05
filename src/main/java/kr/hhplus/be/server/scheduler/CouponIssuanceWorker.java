package kr.hhplus.be.server.scheduler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.coupon.model.Coupon;
import kr.hhplus.be.server.coupon.repository.CouponRepository;
import kr.hhplus.be.server.usercoupon.model.UserCoupon;
import kr.hhplus.be.server.usercoupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponIssuanceWorker {

    private final RedisTemplate<String, String> redisTemplate;
    private final UserCouponRepository userCouponRepository;
    private final CouponRepository couponRepository;
    private final ObjectMapper objectMapper;
    private static final String COUPON_QUEUE_KEY = "coupon:issue:queue";

    @Scheduled(fixedDelay = 50)
    @Transactional
    public void processCouponQueue() {
        String requestPayload = redisTemplate.opsForList().rightPop(COUPON_QUEUE_KEY);

        if (requestPayload != null) {
            log.info("coupon request : {}", requestPayload);
            try {
                JsonNode requestNode = objectMapper.readTree(requestPayload);
                Long userId = requestNode.get("userId").asLong();
                Long couponId = requestNode.get("couponId").asLong();

                // 쿠폰 조회
                Coupon coupon = couponRepository.findById(couponId).orElse(null);
                if (coupon == null) {
                    log.warn("쿠폰 ID {}가 존재하지 않습니다.", couponId);
                }

                // 쿠폰 감소
                int couponCnt = couponRepository.decreaseQuantity(couponId);

                if (couponCnt > 0) {
                    log.info("쿠폰 ID {}가 유저 ID {}에게 성공적으로 발급되었습니다. 남은 coupon 개수 : {}", couponId, userId, coupon.getQuantity());
                    // 쿠폰 등록
                    UserCoupon userCoupon = UserCoupon.create(userId, couponId);
                    userCouponRepository.save(userCoupon);
                } else {
                    log.warn("쿠폰 ID {} 발급에 유저 ID {}가 실패하였습니다. 모두 재고가 모두 소진되었습니다.", couponId, userId);
                }
            } catch (Exception e) {
                log.error("쿠폰 발급 처리 중 예외 발생: {}", requestPayload, e);
            }
        }
    }

}
