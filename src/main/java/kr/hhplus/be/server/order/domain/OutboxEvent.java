package kr.hhplus.be.server.order.domain;

import kr.hhplus.be.server.order.domain.enums.OrderStatus;

import java.time.LocalDateTime;

public class OutboxEvent {
    private Long orderId;
    private String type;
    private String payload;
    private OrderStatus status;
    private LocalDateTime createdAt;

    public OutboxEvent(String type, String payload, OrderStatus status) {
        this.type = type;
        this.payload = payload;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public static OutboxEvent create(String type, String payload, OrderStatus status) {
        return new OutboxEvent(type,payload,status);
    }

    /* Getter */
    public Long getOrderId() {
        return orderId;
    }

    public String getType() {
        return type;
    }

    public String getPayload() {
        return payload;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
