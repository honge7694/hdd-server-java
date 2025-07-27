package kr.hhplus.be.server.orderservice.domain;

import kr.hhplus.be.server.orderservice.domain.enums.OrderStatus;

import java.time.LocalDateTime;

public class OutboxEvent {
    private Long id;
    private Long orderId;
    private String topic;
    private String payload;
    private OrderStatus status;
    private LocalDateTime createdAt;

    public OutboxEvent(String topic, String payload, OrderStatus status) {
        this.topic = topic;
        this.payload = payload;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public static OutboxEvent create(String topic, String payload, OrderStatus status) {
        return new OutboxEvent(topic,payload,status);
    }

    /* Getter */
    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getTopic() {
        return topic;
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

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
