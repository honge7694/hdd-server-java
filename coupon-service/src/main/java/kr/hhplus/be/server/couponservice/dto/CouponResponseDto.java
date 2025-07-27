package kr.hhplus.be.server.couponservice.dto;

import kr.hhplus.be.server.couponservice.model.Coupon;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CouponResponseDto(
        Long couponId,
        String name,
        String code,
        int discountAmount,
        int quantity,
        LocalDate issuedAt,
        LocalDate expiresAt
) {
    public static CouponResponseDto from(Coupon coupon) {
        return CouponResponseDto.builder()
                .couponId(coupon.getId())
                .name(coupon.getName())
                .code(coupon.getCode())
                .discountAmount(coupon.getDiscountAmount())
                .quantity(coupon.getQuantity())
                .issuedAt(coupon.getIssuedAt())
                .expiresAt(coupon.getExpiresAt())
                .build();
    }

    public static CouponResponseDto from(CouponIssuedDto issuedDto) {
        // CouponIssuedDto에는 quantity 정보가 없으므로, 응답에서도 제외하거나 기본값을 설정할 수 있습니다.
        // 여기서는 DTO에 quantity 필드를 추가하는 것이 더 나을 수 있지만, 일단 -1로 표시합니다.
        return CouponResponseDto.builder()
                .couponId(issuedDto.getCouponId())
                .name(issuedDto.getName())
                .code(issuedDto.getCode())
                .discountAmount(issuedDto.getDiscountAmount())
                .quantity(-1) // 재고 정보는 이 응답의 관심사가 아님을 명시
                .issuedAt(null) // issuedAt 정보가 DTO에 없으므로 null 처리
                .expiresAt(issuedDto.getExpiresAt())
                .build();
    }
}
