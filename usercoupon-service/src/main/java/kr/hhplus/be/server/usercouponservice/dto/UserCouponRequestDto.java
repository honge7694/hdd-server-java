package kr.hhplus.be.server.usercouponservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCouponRequestDto {
    private Long userId;
    private Long couponId;
}
