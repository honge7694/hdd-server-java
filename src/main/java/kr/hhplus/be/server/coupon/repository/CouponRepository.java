package kr.hhplus.be.server.coupon.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.coupon.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Coupon c where c.id = :id")
    Optional<Coupon> findByWithLock(@Param("id") Long couponId);

    @Modifying
    @Query("update Coupon c set c.quantity = c.quantity -1 where c.id = :id and c.quantity > 0")
    int decreaseQuantity(@Param("id") Long couponId);
}
