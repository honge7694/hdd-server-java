package kr.hhplus.be.server.usercouponservice.dto;

import kr.hhplus.be.server.usercouponservice.model.UserCoupon;

import java.time.LocalDate;

public record UserCouponResponseDto(
        Long id,
        Long userId,
        Long couponId,
        Boolean used,
        LocalDate createdAt
) {
    public static UserCouponResponseDto from(UserCoupon userCoupon) {
        return new UserCouponResponseDto(
                userCoupon.getId(),
                userCoupon.getUserId(),
                userCoupon.getCouponId(),
                userCoupon.getUsed(),
                userCoupon.getCreatedAt()
        );
    }
}
