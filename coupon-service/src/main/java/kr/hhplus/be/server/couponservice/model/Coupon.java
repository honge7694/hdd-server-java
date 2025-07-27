package kr.hhplus.be.server.couponservice.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.global.exception.ConflictException;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
public class Coupon {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long id;

    private String name;
    private String code;
    private int discountAmount;
    private int quantity;
    private LocalDate issuedAt;
    private LocalDate expiresAt;

    protected Coupon() {}

    public Coupon(String name, String code, int discountAmount, int quantity, LocalDate issuedAt, LocalDate expiresAt) {
        this.name = name;
        this.code = code;
        this.discountAmount = discountAmount;
        this.quantity = quantity;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }

    public static Coupon create(String name, String code, int discountAmount, int quantity, LocalDate issuedAt, LocalDate expiresAt) {
        return new Coupon(name, code, discountAmount, quantity, issuedAt, expiresAt);
    }

    /* 비즈니스 로직 */
    public void reduceCoupon() {
        if (quantity < 1) {
            throw new ConflictException("쿠폰 재고가 소진되었습니다.");
        }
        quantity -= 1;
    }
    public void addCoupon() {
        quantity += 1;
    }
}
