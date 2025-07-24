package kr.hhplus.be.server.couponservice.service;

import kr.hhplus.be.server.couponservice.dto.CouponIssueRequestDto;
import kr.hhplus.be.server.couponservice.dto.CouponIssuedDto;
import kr.hhplus.be.server.couponservice.dto.CouponRequestDto;
import kr.hhplus.be.server.couponservice.dto.CouponResponseDto;
import kr.hhplus.be.server.couponservice.model.Coupon;
import kr.hhplus.be.server.couponservice.repository.CouponRepository;
import kr.hhplus.be.server.global.exception.NotFoundException;
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
        return CouponResponseDto.from(coupon);
    }

    /**
     * 쿠폰을 발급하고 재고를 감소시킵니다.
     * 선착순 쿠폰과 같이 동시성 제어가 필요한 경우 비관적 락을 사용합니다.
     * @param request 쿠폰 발급 요청 DTO
     * @return 발급된 쿠폰 정보를 담은 내부 DTO
     */
    @Transactional
    public CouponIssuedDto issueCoupon(CouponIssueRequestDto request) {
        Coupon coupon = couponRepository.findByWithLock(request.getCouponId())
                .orElseThrow(() -> new NotFoundException("Coupon not found"));
        coupon.reduceCoupon();
        return CouponIssuedDto.from(coupon);
    }

    /**
     * 쿠폰 발급 실패 시 재고를 원복하는 보상 트랜잭션 메서드입니다.
     * @param couponId 보상할 쿠폰 ID
     */
    @Transactional
    public void compensateCouponIssuance(Long couponId) {
        Coupon coupon = couponRepository.findByWithLock(couponId)
                .orElseThrow(() -> new NotFoundException("Coupon not found"));
        coupon.addCoupon();
    }
}
