package kr.hhplus.be.server.order.domain;

import kr.hhplus.be.server.order.domain.enums.OrderStatus;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private Long id;
    private Long userId;
    private Long userCouponId;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Order(Long userId, Long userCouponId, OrderStatus status) {
        this.userId = userId;
        this.userCouponId = userCouponId;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Order(Long userId, Long userCouponId, OrderStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.userCouponId = userCouponId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Order create(Long userId, Long userCouponId, OrderStatus status) {
        return new Order(userId, userCouponId, status);
    }

    // entity -> domain
    public static Order create(Long userId, Long userCouponId, OrderStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Order(userId, userCouponId, status, createdAt, updatedAt);
    }

    public Order assignId(long orderId) {
        Order orderWithId = new Order(this.userId, this.userCouponId, this.status, this.createdAt, this.updatedAt);
        orderWithId.id = orderId;
        return orderWithId;
    }

    /* 비즈니스 로직 */
    public void changeStatus(OrderStatus status) {
        this.status = status;
    }

    public void applyCoupon(Long userCouponId) {
        this.userCouponId = userCouponId;
    }

    public Long calculateTotalPrice(List<OrderItem> orderItems) {
        Long total = 0L;
        for(OrderItem orderItem : orderItems) {
            total += orderItem.getQuantity() * orderItem.getPrice();
        }
        return total;
    }

//    TODO: 쿠폰 생기면 추가
//    public Long calculateDiscountPrice(List<OrderItem> orderItems) {
//
//    }

    /* Getter */
    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public long getUserCouponId() {
        return userCouponId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
