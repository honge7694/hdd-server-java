package kr.hhplus.be.server.orderservice.infra.gateway.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.orderservice.domain.Order;
import kr.hhplus.be.server.orderservice.domain.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table
public class OrderEntity {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;
    private Long userId;

    @Column(nullable = true)
    private Long userCouponId;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected OrderEntity() {}

    public OrderEntity(Long userId, Long userCouponId, OrderStatus orderStatus, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.userCouponId = userCouponId;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public OrderEntity(long id, long userId, long userCouponId, OrderStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.userCouponId = userCouponId;
        this.orderStatus = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static OrderEntity fromDomain(Order order) {
        return new OrderEntity(
                order.getUserId(),
                order.getUserCouponId(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    public Order toDomain() {
        return Order.create(userId, userCouponId, orderStatus, createdAt, updatedAt).assignId(id);
    }

    public static OrderEntity fromExistingDomain(Order order) {
        return new OrderEntity(
                order.getId(),
                order.getUserId(),
                order.getUserCouponId(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}

