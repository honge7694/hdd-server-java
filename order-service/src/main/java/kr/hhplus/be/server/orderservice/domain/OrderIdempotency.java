package kr.hhplus.be.server.orderservice.domain;

import java.time.LocalDateTime;

public class OrderIdempotency {
    private Long id;
    private Long userId;
    private String idempotencyKey;
    private LocalDateTime createdAt;

    public OrderIdempotency(Long userId, String idempotencyKey) {
        this.userId = userId;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = LocalDateTime.now();
    }

    public static OrderIdempotency create(Long userId, String idempotencyKey) {
        return new OrderIdempotency(userId, idempotencyKey);
    }

    public OrderIdempotency assignId(Long orderIdempotencyId) {
        OrderIdempotency orderIdempotency = new OrderIdempotency(userId, idempotencyKey);
        orderIdempotency.id = orderIdempotencyId;
        return orderIdempotency;
    }

    /* Getter */
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
