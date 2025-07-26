package kr.hhplus.be.server.orderservice.infra.gateway.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import kr.hhplus.be.server.orderservice.domain.OrderIdempotency;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class OrderIdempotencyEntity {

    @Id
    @GeneratedValue
    @Column(name = "order_idempotency_id")
    private Long id;

    private Long userId;

    @Column(unique = true)
    private String idempotencyKey;

    private LocalDateTime createdAt;


    protected OrderIdempotencyEntity() {}

    public OrderIdempotencyEntity(Long userId, String idempotencyKey) {
        this.userId = userId;
        this.idempotencyKey = idempotencyKey;
        this.createdAt = LocalDateTime.now();
    }

    public static OrderIdempotencyEntity fromDomain(OrderIdempotency orderIdempotency) {
        return new OrderIdempotencyEntity(
                orderIdempotency.getUserId(),
                orderIdempotency.getIdempotencyKey()
        );
    }

    public OrderIdempotency toDomain() {
        return OrderIdempotency.create(userId, idempotencyKey).assignId(id);
    }
}

