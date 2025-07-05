package kr.hhplus.be.server.usercoupon.service;

import kr.hhplus.be.server.coupon.model.Coupon;
import kr.hhplus.be.server.coupon.repository.CouponRepository;
import kr.hhplus.be.server.global.exception.ConflictException;
import kr.hhplus.be.server.global.exception.NotFoundException;
import kr.hhplus.be.server.user.application.usecase.port.out.UserRepository;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.usercoupon.dto.UserCouponRequestDto;
import kr.hhplus.be.server.usercoupon.dto.UserCouponResponseDto;
import kr.hhplus.be.server.usercoupon.model.UserCoupon;
import kr.hhplus.be.server.usercoupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCouponService {

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String COUPON_APPLICANTS_KEY_PREFIX = "coupon:applicants:";
    private static final String COUPON_QUEUE_KEY = "coupon:issue:queue";

    @Transactional
    public UserCouponResponseDto registerUserCoupon(UserCouponRequestDto userCouponRequestDto) {

        // 유저 조회
        Long userId = userCouponRequestDto.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("유저가 존재하지 않습니다."));

        // 쿠폰 조회
        Long couponId = userCouponRequestDto.getCouponId();
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new NotFoundException("해당 쿠폰이 존재하지 않습니다."));

        // 비관적 락
//        Coupon coupon = couponRepository.findByWithLock(couponId)
//                .orElseThrow(() -> new NotFoundException("해당 쿠폰이 존재하지 않습니다."));
        // 낙관적 락 (원자적 업데이트)
//        int couponStock = couponRepository.decreaseQuantity(couponId);
//        if (couponStock == 0) {
//            throw new ConflictException("쿠폰이 모두 소진되었습니다.");
//        }

        // 쿠폰 저장
        if (userCouponRepository.existsByUserIdAndCouponId(user.getId(), coupon.getId())) {
            throw new ConflictException("이미 해당 쿠폰을 발급받은 사용자입니다.");
        }
        UserCoupon userCoupon = UserCoupon.create(user.getId(), coupon.getId());
        userCouponRepository.save(userCoupon);
        coupon.reduceCoupon();

        return new UserCouponResponseDto(
                userCoupon.getId(),
                userCoupon.getUserId(),
                userCoupon.getCouponId(),
                userCoupon.getUsed(),
                userCoupon.getCreatedAt()
        );
    }

    /**
     * 쿠폰 발급 요청을 Redis 큐에 추가합니다.
     *
     * @param userId    요청한 유저 ID
     * @param couponId  발급 받을 쿠폰 ID
     */
    public void queueCouponRequest(Long userId, Long couponId) {
        log.info("queue에 쿠폰 발급 요청을 전달 받았습니다.");
        String applicantsKey = COUPON_APPLICANTS_KEY_PREFIX + couponId;

        // 이미 멤버가 존재하면 0을 반환합니다.
        Long addedCount = redisTemplate.opsForSet().add(applicantsKey, String.valueOf(userId));

        if (addedCount == null || addedCount == 0) {
            throw new ConflictException("이미 해당 쿠폰을 발급받았거나, 요청 중입니다.");
        }

        // 큐에 삽입
        String requestPayload = "{\"userId\":" + userId + ", \"couponId\":" + couponId + "}";
        redisTemplate.opsForList().leftPush(COUPON_QUEUE_KEY, requestPayload);
    }
}
