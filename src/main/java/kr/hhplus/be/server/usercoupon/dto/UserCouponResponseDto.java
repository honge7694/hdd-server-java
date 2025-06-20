package kr.hhplus.be.server.usercoupon.dto;

import java.time.LocalDate;

public record UserCouponResponseDto(
        Long id,
        Long userId,
        Long couponId,
        Boolean used,
        LocalDate createdAt
) {
}
