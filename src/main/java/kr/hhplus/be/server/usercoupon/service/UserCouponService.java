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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCouponService {

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

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
}
