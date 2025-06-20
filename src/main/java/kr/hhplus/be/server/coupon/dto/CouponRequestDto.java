package kr.hhplus.be.server.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponRequestDto {
    private String name;
    private String code;
    private int discountAmount;
    private int quantity;
    private LocalDate issuedAt;
    private LocalDate expiresAt;
}
