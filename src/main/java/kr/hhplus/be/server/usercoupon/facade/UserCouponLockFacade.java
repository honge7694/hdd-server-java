package kr.hhplus.be.server.usercoupon.facade;

import kr.hhplus.be.server.usercoupon.dto.UserCouponRequestDto;
import kr.hhplus.be.server.usercoupon.dto.UserCouponResponseDto;
import kr.hhplus.be.server.usercoupon.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCouponLockFacade {

    private final RedisTemplate<String, String> redisTemplate;
    private final UserCouponService userCouponService;
    private final RedissonClient redissonClient;

    @Value("${lock.max-retries}")
    private int maxRetries;

    public UserCouponResponseDto registerUserCouponLock(UserCouponRequestDto userCouponRequestDto) throws InterruptedException {
        String lockKey = "lock:coupon:"+userCouponRequestDto.getCouponId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean isLocked = lock.tryLock(10, 15, TimeUnit.SECONDS);

            // 락 획득에 실패한 경우
            if (!isLocked) {
                log.error("락 획득 실패, lockKey : {}", lockKey);
                throw new RuntimeException("쿠폰 발급에 실패하였습니다.");
            }

            // 락 획득에 성공한 경우
            return userCouponService.registerUserCoupon(userCouponRequestDto);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("락 대기 중 인터럽트 발생!", e);
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}

