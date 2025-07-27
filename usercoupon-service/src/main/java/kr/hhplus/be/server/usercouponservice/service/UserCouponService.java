package kr.hhplus.be.server.usercouponservice.service;

import kr.hhplus.be.server.global.exception.ConflictException;
import kr.hhplus.be.server.usercouponservice.dto.UserCouponDto;
import kr.hhplus.be.server.usercouponservice.dto.UserCouponResponseDto;
import kr.hhplus.be.server.usercouponservice.model.UserCoupon;
import kr.hhplus.be.server.usercouponservice.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;

    @Transactional
    public void issueUserCoupon(UserCouponDto userCouponDto) {
        log.info("issueUserCoupon: {}", userCouponDto);
        Long userId = userCouponDto.getUserId();
        Long couponId = userCouponDto.getCouponId();

        if (userCouponRepository.existsByUserIdAndCouponId(userId, couponId)) {
            throw new ConflictException("이미 해당 쿠폰을 발급받은 사용자입니다.");
        }

        UserCoupon userCoupon = UserCoupon.create(userId, couponId);
        userCouponRepository.save(userCoupon);
    }

    @Transactional(readOnly = true)
    public List<UserCouponResponseDto> getUserCouponsByUserId(Long userId) {
        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);
        return userCoupons.stream()
                .map(UserCouponResponseDto::from)
                .collect(Collectors.toList());
    }
}
