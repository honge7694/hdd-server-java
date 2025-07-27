package kr.hhplus.be.server.couponservice.dto;

import kr.hhplus.be.server.couponservice.model.Coupon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 쿠폰 발급 성공 이벤트 및 서비스 계층의 결과 반환을 위한 DTO입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponIssuedDto {
    private Long couponId;
    private String name;
    private String code;
    private int discountAmount;
    private LocalDate expiresAt;

    public static CouponIssuedDto from(Coupon coupon) {
        return CouponIssuedDto.builder()
                .couponId(coupon.getId())
                .name(coupon.getName())
                .code(coupon.getCode())
                .discountAmount(coupon.getDiscountAmount())
                .expiresAt(coupon.getExpiresAt())
                .build();
    }
}
