package kr.hhplus.be.server.orderservice.domain;

import kr.hhplus.be.server.orderservice.domain.enums.OrderStatus;

import java.time.LocalDateTime;

public class OrderHistory {
    private Long id;
    private Long orderId;
    private OrderStatus status;
    private LocalDateTime createdAt;

    public OrderHistory(Long orderId, OrderStatus status, LocalDateTime createAt) {
        this.orderId = orderId;
        this.status = status;
        this.createdAt = createAt;
    }

    public static OrderHistory create(Long orderId, OrderStatus status, LocalDateTime createAt) {
        return new OrderHistory(orderId, status, createAt);
    }

    public OrderHistory assignId(Long orderHistoryId) {
        OrderHistory orderHistoryWithId = new OrderHistory(this.orderId, this.status, this.createdAt);
        orderHistoryWithId.id = orderHistoryId;
        return orderHistoryWithId;
    }

    /* Getter */

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
