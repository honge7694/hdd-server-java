package kr.hhplus.be.server.usercoupon.repository;

import kr.hhplus.be.server.usercoupon.model.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    boolean existsByUserIdAndCouponId(Long userId, Long couponId);
}
