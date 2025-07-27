package kr.hhplus.be.server.orderservice.infra.gateway.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.orderservice.domain.OrderHistory;
import kr.hhplus.be.server.orderservice.domain.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table
public class OrderHistoryEntity {

    @Id @GeneratedValue
    @Column(name = "order_history_id")
    private Long id;
    private Long orderId;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;

    protected OrderHistoryEntity() {}

    public OrderHistoryEntity(Long orderId, OrderStatus orderStatus, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
    }

    public static OrderHistoryEntity fromDomain(OrderHistory orderHistory) {
        return new OrderHistoryEntity(
                orderHistory.getOrderId(),
                orderHistory.getStatus(),
                orderHistory.getCreatedAt()
        );
    }

    public OrderHistory toDomain() {
        return OrderHistory.create(orderId, orderStatus, createdAt).assignId(id);
    }
}
