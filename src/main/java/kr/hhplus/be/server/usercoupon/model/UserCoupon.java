package kr.hhplus.be.server.usercoupon.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
public class UserCoupon {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_coupon_id")
    private Long id;

    private Long userId;

    private Long couponId;

    private Boolean used;

    private LocalDate createdAt;

    private LocalDateTime usedAt;

    protected UserCoupon() {}

    public UserCoupon(Long userId, Long couponId) {
        this.userId = userId;
        this.couponId = couponId;
        this.used = false;
        this.createdAt = LocalDate.now();
    }

    public static UserCoupon create(Long userId, Long CouponId) {
        return new UserCoupon(userId, CouponId);
    }

    /* 비즈니스 로직 */
    public void usedAt() {
        this.used = true;
        this.usedAt = LocalDateTime.now();
    }
}
