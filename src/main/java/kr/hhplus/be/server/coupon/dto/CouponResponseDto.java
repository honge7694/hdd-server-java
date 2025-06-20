package kr.hhplus.be.server.coupon.dto;

import java.time.LocalDate;

public record CouponResponseDto(
        Long couponId,
        String name,
        String code,
        int discountAmount,
        int quantity,
        LocalDate issuedAt,
        LocalDate expiresDate
) {

}
