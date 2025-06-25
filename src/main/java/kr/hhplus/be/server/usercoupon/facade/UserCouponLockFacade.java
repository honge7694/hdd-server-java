package kr.hhplus.be.server.usercoupon.facade;

import kr.hhplus.be.server.usercoupon.dto.UserCouponRequestDto;
import kr.hhplus.be.server.usercoupon.dto.UserCouponResponseDto;
import kr.hhplus.be.server.usercoupon.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCouponLockFacade {

    private final RedisTemplate<String, String> redisTemplate;
    private final UserCouponService userCouponService;

    @Value("${lock.max-retries}")
    private int maxRetries;

    public UserCouponResponseDto registerUserCouponLock(UserCouponRequestDto userCouponRequestDto) throws InterruptedException {
        String lockKey = "lock:coupon:"+userCouponRequestDto.getCouponId();
        Random random = new Random();
        int randomValue = random.nextInt(401) + 100;

        for (int i = 1; i <= maxRetries; i++) {
            // lockKey가 없다면 lock을 등록
            if (Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", Duration.ofSeconds(5)))) {
                try {
                    // 쿠폰 등록
                    return userCouponService.registerUserCoupon(userCouponRequestDto);
                } finally {
                    redisTemplate.delete(lockKey);
                }
            } else {
                if (i == maxRetries) {
                    throw new RuntimeException("최대 재시도 횟수 초과");
                }
                log.info("재시도 횟수 : {}", i);
                Thread.sleep(randomValue);
            }
        }
        throw new RuntimeException("쿠폰 발급 처리에 실패했습니다.");
    }
}

