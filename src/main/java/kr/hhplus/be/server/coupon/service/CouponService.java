package kr.hhplus.be.server.coupon.service;

import kr.hhplus.be.server.coupon.dto.CouponRequestDto;
import kr.hhplus.be.server.coupon.dto.CouponResponseDto;
import kr.hhplus.be.server.coupon.model.Coupon;
import kr.hhplus.be.server.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {


    private final CouponRepository couponRepository;

    @Transactional
    public CouponResponseDto createCoupon(CouponRequestDto couponRequestDto) {
        Coupon coupon = Coupon.create(
                couponRequestDto.getName(),
                couponRequestDto.getCode(),
                couponRequestDto.getDiscountAmount(),
                couponRequestDto.getQuantity(),
                couponRequestDto.getIssuedAt(),
                couponRequestDto.getExpiresAt());
        couponRepository.save(coupon);
        return new CouponResponseDto(
                coupon.getId(),
                coupon.getName(),
                coupon.getCode(),
                coupon.getDiscountAmount(),
                coupon.getQuantity(),
                coupon.getIssuedAt(),
                coupon.getExpiresAt());
    }
}
